package com.jtm.plugin.core.domain.model

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.math.ceil

data class PageSupport<T> (val content: MutableList<T>,
                           val pageNumber: Int,
                           val pageSize: Int,
                           val totalElements: Int) {

    @JsonProperty
    fun totalPages(): Int {
        return if (pageSize > 0) ceil((totalElements / pageSize).toDouble()).toInt() else 0
    }

    @JsonProperty
    fun first(): Boolean {
        return pageNumber == 1
    }

    @JsonProperty
    fun last(): Boolean {
        return (pageNumber + 1) * pageSize >= totalElements
    }
}