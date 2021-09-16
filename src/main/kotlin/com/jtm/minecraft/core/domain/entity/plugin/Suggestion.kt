package com.jtm.minecraft.core.domain.entity.plugin

import com.jtm.minecraft.core.domain.dto.SuggestionDto
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("suggestions")
data class Suggestion(val id: UUID = UUID.randomUUID(), val pluginId: UUID, val accountId: UUID, var comment: String, var likes: Int = 0, val created: Long = System.currentTimeMillis()) {

    constructor(accountId: UUID, dto: SuggestionDto): this( pluginId = dto.pluginId, accountId = accountId, comment = dto.comment)

    fun updateComment(comment: String): Suggestion {
        this.comment = comment
        return this
    }
}
