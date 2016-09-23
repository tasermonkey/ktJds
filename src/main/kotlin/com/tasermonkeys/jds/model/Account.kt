package com.tasermonkeys.jds.model

import java.time.Instant

data class Account(val accountName: String,
                   val email: String,
                   val masterRole: MasterRole,
                   val firstName: String,
                   val lastName: String,
                   override var id: String?,
                   override var owner: String,
                   override val createdAt: Instant,
                   override val creator: String,
                   override var lastUpdatedAt: Instant,
                   override var lastUpdatedBy: String,
                   override var visibility: Set<String> // groups this user is a member of
): JdsItemReference, BaseDataItem, Authorizable {
    constructor(accountName: String) : this(accountName, "", MasterRole.ROLE_USER, "", "", null, "", Instant.now(), "", Instant.now(), "", emptySet())
}