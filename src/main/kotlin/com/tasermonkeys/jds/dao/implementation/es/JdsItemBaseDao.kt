package com.tasermonkeys.jds.dao.implementation.es

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.tasermonkeys.jds.dao.Dao
import com.tasermonkeys.jds.dao.DaoAuthorizationManager
import com.tasermonkeys.jds.dao.DaoSearchCriteria
import com.tasermonkeys.jds.exceptions.NotAuthorizedException
import com.tasermonkeys.jds.model.JdsItem
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.Client
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.data.domain.*

@Scope("request")
abstract class JdsItemBaseDao<T : JdsItem>(val indexName: String, val typeName: String) : Dao<T> {

    @Autowired(required = true)
    lateinit override var authorization: DaoAuthorizationManager<T>

    //@Autowired(required = true)
    lateinit var client: Client

    // Allows subclasses to specify differences between database storage and API classes
    // Defaults assumes same.
    open fun fromElastic(json: String): T = defFromElastic(json)

    open fun toElastic(obj: T): String = defToElastic(obj)

    override fun findOne(id: String): T? {
        val response = queryById(id).get()
        if (response.hits.totalHits() == 0L) {
            return null
        }
        return fromElastic(response.hits.getAt(0).sourceAsString())
    }

    override fun delete(entity: T) = delete(entity.id!!)

    override fun delete(entities: MutableIterable<T>) = entities.forEach { delete(it) }

    override fun delete(id: String) {
        val obj = findOne(id) ?: throw NotAuthorizedException("User not authorized to view $id")
        val response = client.prepareDelete(indexName, typeName, obj.id.toString()).get()
        if (response.isFound)
            throw IndexOutOfBoundsException("No element by the id $id was found.")
    }

    override fun findAll(sort: Sort?): MutableIterable<T> = findAll(pageable = PageRequest(0, 10000, sort))

    override fun findAll(pageable: Pageable?): Page<T> {
        val res = executeAll().hits.map { fromElastic(it.sourceAsString) }.toMutableList()
        return PageImpl(res, pageable, res.size.toLong())
    }

    override fun findAll(ids: MutableIterable<String>): MutableIterable<T> = find(DaoSearchCriteria(ids = ids.toSet()), null).toMutableList()

    override fun deleteAll() = deleteByCriteria(DaoSearchCriteria())

    override fun <S : T> save(entities: MutableIterable<S>): MutableIterable<S> = entities.map { save(it) }.toMutableList()

    @Suppress("UNCHECKED_CAST")
    override fun <S : T> save(entity: S): S = (if (entity.id != null) update(entity) else store(entity)) as S

    override fun exists(id: String): Boolean = findOne(id) != null

    override fun count(): Long = client.prepareSearch(indexName).setSize(0).setTypes(typeName)
            .setQuery(QueryBuilders.boolQuery().must(visQueryBuilder(criteriaVisibility(DaoSearchCriteria()))))
            .get().hits.totalHits

    ///////////////////////////////////

    override fun find(criteria: DaoSearchCriteria, page: Pageable?): List<T> =
            executeQuery(criteria).hits.map { fromElastic(it.sourceAsString) }


    override fun findAll(): MutableIterable<T> = findAll(pageable = null)


    private fun store(item: T): T = findOne(client.prepareIndex(indexName, typeName).setSource(toElastic(item)).get().id)!!

    private fun update(item: T): T =
            findOne(client.prepareUpdate(indexName, typeName, item.id).setDoc(toElastic(item)).get().id)!!

    override fun deleteByCriteria(criteria: DaoSearchCriteria) {
        val queryResponse = executeQuery(criteria)
        val bulkOperation = client.prepareBulk()
        queryResponse.hits.forEach { bulkOperation.add(client.prepareDelete(indexName, typeName, it.id)) }
        val response = bulkOperation.get()
        if (response.hasFailures())
            throw RuntimeException(response.buildFailureMessage())
    }

    protected fun criteriaVisibility(criteria: DaoSearchCriteria): Collection<String> =
            if (criteria.visibilities.isEmpty()) authorization.runningUserVisibilities
            else authorization.runningUserVisibilities.intersect(criteria.visibilities)

    protected fun executeQuery(criteria: DaoSearchCriteria): SearchResponse =
            if (criteria.isEmpty())
                executeAll()
            else
                client.prepareSearch(indexName)
                        .setTypes(typeName)
                        .setQuery(QueryBuilders.boolQuery().must(visQueryBuilder(criteriaVisibility(criteria)))
                                .must(toQueryBuilder(criteria)))
                        .execute().actionGet()

    protected fun executeAll(): SearchResponse = client.prepareSearch(indexName)
            .setTypes(typeName)
            .setQuery(QueryBuilders.boolQuery().must(visQueryBuilder(criteriaVisibility(DaoSearchCriteria()))))
            .execute().actionGet()

    protected fun toQueryBuilder(criteria: DaoSearchCriteria): QueryBuilder {
        val query = QueryBuilders.boolQuery()
        if (criteria.owner.isNotEmpty())
            query.must(QueryBuilders.termsQuery("owner", criteria.owner))
        if (!criteria.lastUpdate.isEmpty())
            query.must(QueryBuilders.rangeQuery("lastUpdatedAt")
                    .to(criteria.lastUpdate.endInclusive).from(criteria.lastUpdate.start))
        if (criteria.ids.isNotEmpty())
            query.must(QueryBuilders.termsQuery("id", criteria.ids))
        return query
    }

    protected fun queryById(id: String): SearchRequestBuilder = client.prepareSearch(indexName)
            .setTypes(typeName)
            .setQuery(QueryBuilders.boolQuery().must(createESCriteria())
                    .must(QueryBuilders.termQuery("id", id)))

    protected fun createESCriteria(): QueryBuilder = QueryBuilders.boolQuery().must(visQueryBuilder(authorization.runningUserVisibilities))
    protected fun visQueryBuilder(vis: Collection<String>): QueryBuilder = QueryBuilders.termsQuery("visibility", vis.toMutableSet())
}

private val mapper = jacksonObjectMapper()
private fun <T> defFromElastic(json: String): T = mapper.readValue(json, object : TypeReference<T>() {})
private fun <T> defToElastic(obj: T): String = mapper.writeValueAsString(obj)