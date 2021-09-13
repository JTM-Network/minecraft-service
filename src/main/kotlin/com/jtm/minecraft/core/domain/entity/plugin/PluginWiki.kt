package com.jtm.minecraft.core.domain.entity.plugin

import com.jtm.minecraft.core.domain.dto.WikiTopicDto
import com.jtm.minecraft.core.domain.model.WikiTopic
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("plugin_wiki")
data class PluginWiki(
    @Id val id: UUID,
    val topics: MutableMap<String, WikiTopic> = mutableMapOf()) {

    fun addWiki(dto: WikiTopicDto): WikiTopic {
        val topic = WikiTopic(dto)
        this.topics[dto.name] = topic
        return topic
    }

    fun updateWiki(dto: WikiTopicDto): WikiTopic? {
        val topic = this.topics[dto.name] ?: return null
        topic.title = dto.title
        topic.html = dto.html
        return topic
    }

    fun exists(name: String): Boolean {
        return this.topics.containsKey(name)
    }

    fun getWiki(name: String): WikiTopic? {
        return this.topics[name]
    }

    fun removeWiki(name: String): PluginWiki {
        this.topics.remove(name)
        return this
    }
}