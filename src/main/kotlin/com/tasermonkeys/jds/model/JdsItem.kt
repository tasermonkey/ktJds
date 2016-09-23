package com.tasermonkeys.jds.model

import java.time.ZonedDateTime

interface JdsItem : JdsItemReference, BaseDataItem, Authorizable {
    var title: String
    var description: String
}