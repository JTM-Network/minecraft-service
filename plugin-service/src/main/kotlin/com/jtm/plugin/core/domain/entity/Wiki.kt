package com.jtm.plugin.core.domain.entity

import com.jtm.plugin.core.domain.dto.WikiTopicDto
import com.jtm.plugin.core.domain.model.WikiTopic
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("wikis")
data class Wiki(@Id val id: UUID, val topics: MutableMap<String, WikiTopic> = mutableMapOf()) {

    fun addWiki(dto: WikiTopicDto): WikiTopic {
        val topic = WikiTopic(dto)
        this.topics[dto.name] = topic
        return topic
    }

    fun updateWiki(dto: WikiTopicDto): WikiTopic? {
        val topic = this.topics[dto.name] ?: return null
        this.topics[dto.name] = topic.update(dto)
        return topic
    }

    fun exists(name: String): Boolean {
        return this.topics.containsKey(name)
    }

    fun getWiki(name: String): WikiTopic? {
        return this.topics[name]
    }

    fun removeWiki(name: String): Wiki {
        this.topics.remove(name)
        return this
    }
}