package com.tasermonkeys.jds.dao

import com.tasermonkeys.jds.model.JdsItemReference
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;


interface Dao<T : JdsItemReference> : ElasticsearchCrudRepository<T, String> {
    val authorization: DaoAuthorizationManager<T>
    fun find(criteria: DaoSearchCriteria = DaoSearchCriteria(), page: Pageable?): List<T>
    fun deleteByCriteria(criteria: DaoSearchCriteria = DaoSearchCriteria())
    override fun findAll(sort: Sort?): MutableIterable<T>?
    override fun findAll(pageable: Pageable?): Page<T>
}