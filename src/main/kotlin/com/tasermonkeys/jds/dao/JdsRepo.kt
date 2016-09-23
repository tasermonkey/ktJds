package com.tasermonkeys.jds.dao

import com.tasermonkeys.jds.model.JdsItemReference
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository


interface JdsRepo<T : JdsItemReference> : ElasticsearchCrudRepository<T, String> {

}