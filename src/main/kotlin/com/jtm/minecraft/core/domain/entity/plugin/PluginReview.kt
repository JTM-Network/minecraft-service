package com.jtm.minecraft.core.domain.entity.plugin

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("plugin_review")
data class PluginReview(
    @Id val id: UUID = UUID.randomUUID(),
    val accountId: UUID,
    val pluginId: UUID,
    var rating: Double,
    var comment: String,
    var updated: Long = System.currentTimeMillis(),
    val created: Long = System.currentTimeMillis()) {

    fun updateRating(rating: Double): PluginReview {
        this.rating = rating
        return this
    }

    fun updateComment(comment: String): PluginReview {
        this.comment = comment
        return this
    }
}