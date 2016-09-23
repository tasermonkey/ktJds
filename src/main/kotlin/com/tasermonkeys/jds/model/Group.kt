package com.tasermonkeys.jds.model

import java.time.Instant

data class Group(val name: String,
                 val description: String,
                 val startDateTime: Instant,
                 override var id: String?,
                 override var owner: String,
                 override val createdAt: Instant,
                 override val creator: String,
                 override var lastUpdatedAt: Instant,
                 override var lastUpdatedBy: String,
                 override var visibility: Set<String>  // this will be == a members list...this could also mean a group could be a member of a group
): JdsItemReference, BaseDataItem, Authorizable