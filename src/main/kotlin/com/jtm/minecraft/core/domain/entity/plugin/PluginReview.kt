package com.jtm.minecraft.core.domain.entity.plugin

import com.jtm.minecraft.core.domain.dto.PluginReviewDto
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

    fun update(dto: PluginReviewDto): PluginReview {
        this.rating = dto.rating
        this.comment = dto.comment
        return this
    }
}