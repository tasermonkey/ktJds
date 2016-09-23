package com.tasermonkeys.jds.controller

import com.tasermonkeys.jds.dao.implementation.es.TicketDao
import com.tasermonkeys.jds.model.Ticket
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ticket")
@Scope("request")
class TicketController @Autowired constructor(val repository: TicketDao) {

    @RequestMapping("/{id}")
    fun getById(@PathVariable id: String): Ticket? = repository.findOne(id)

}