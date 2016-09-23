package com.tasermonkeys.jds.dao.implementation.es

import com.tasermonkeys.jds.dao.DaoAuthorizationManager
import com.tasermonkeys.jds.model.Account
import com.tasermonkeys.jds.model.JdsItemReference
import com.tasermonkeys.jds.model.MasterRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.security.core.Authentication

@Component
@Scope("request")
open class DaoAuthorizationManagerES<in T: JdsItemReference> : DaoAuthorizationManager<T> {
    @Autowired(required = true)
    lateinit var authentication: Authentication

    override val runningUser: Account
        get() = getAccountByName(authentication.name)
    override val runningUserVisibilities: Set<String>
        get() = throw UnsupportedOperationException()

    override fun canWrite(item: T): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun canRead(item: T): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    private fun getAccountByName(name: String) = Account(accountName = name)
}