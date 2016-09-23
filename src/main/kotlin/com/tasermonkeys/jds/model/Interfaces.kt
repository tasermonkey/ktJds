package com.tasermonkeys.jds.model

import java.time.Instant

interface JdsItemReference {
    var id: String?
}

interface BaseDataItem {
    var owner: String
    val createdAt: Instant
    val creator: String
    var lastUpdatedAt: Instant
    var lastUpdatedBy: String
}

interface Authorizable {
    var visibility: Set<String>
}