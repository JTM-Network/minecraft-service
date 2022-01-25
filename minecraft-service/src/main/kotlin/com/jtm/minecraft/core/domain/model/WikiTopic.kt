package com.jtm.minecraft.core.domain.model

import com.jtm.minecraft.core.domain.dto.WikiTopicDto

data class WikiTopic(
    val name: String,
    var title: String,
    var html: String,
    val edited: Long = System.currentTimeMillis(),
    val created: Long = System.currentTimeMillis()) {

    constructor(dto: WikiTopicDto): this(dto.name, dto.title, dto.html)

    fun update(dto: WikiTopicDto): WikiTopic {
        this.title = dto.title
        this.html = dto.html
        return this
    }
}
