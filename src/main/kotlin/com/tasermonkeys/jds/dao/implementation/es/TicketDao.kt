package com.tasermonkeys.jds.dao.implementation.es

import com.tasermonkeys.jds.model.Ticket
import org.springframework.stereotype.Repository


@Repository
open class TicketDao() : JdsItemBaseDao<Ticket>("jds", "ticket") {
}