package com.tasermonkeys.jds.model

import java.time.Instant
data class Ticket(var sendingGroup: String, var recipientGroup: String, var assignee: String,
                  var status: TicketStatus,
                  override var title: String, override var description: String,
                  override var id: String?, override var owner: String,
                  override var createdAt: Instant, override var creator: String,
                  override var lastUpdatedAt: Instant, override var lastUpdatedBy: String,
                  override var visibility: Set<String>) : JdsItem {
}

enum class TicketStatus {
    OPEN,
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    AWAITING_CLARIFICATION,
    CLOSED,
    CANCELED
}
