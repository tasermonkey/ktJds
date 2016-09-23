package com.tasermonkeys.jds.dao

import java.time.Instant

data class InstantRange(override val start: Instant,
                        override val endInclusive: Instant) : ClosedRange<Instant>

fun emptyInstanceRange(): InstantRange = InstantRange(Instant.MAX, Instant.MIN)

data class DaoSearchCriteria(val ids: Set<String> = setOf(), val visibilities: Set<String> = setOf(), val owner: Set<String> = setOf(),
                             val lastUpdate: InstantRange = emptyInstanceRange()) {
    constructor(visibility: String): this(visibilities = setOf(visibility))

    fun isEmpty():Boolean = ids.isEmpty() && visibilities.isEmpty() && owner.isEmpty() && lastUpdate.isEmpty()
}