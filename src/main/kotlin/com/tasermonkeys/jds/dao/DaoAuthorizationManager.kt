package com.tasermonkeys.jds.dao

import com.tasermonkeys.jds.model.Account
import com.tasermonkeys.jds.model.JdsItemReference
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

interface DaoAuthorizationManager<in T: JdsItemReference> {
    val runningUser: Account
    val runningUserVisibilities: Set<String>
    fun canWrite(item: T): Boolean
    fun canRead(item: T): Boolean
}