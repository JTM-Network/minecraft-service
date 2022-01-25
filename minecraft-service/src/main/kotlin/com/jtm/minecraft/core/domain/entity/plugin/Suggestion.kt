package com.jtm.minecraft.core.domain.entity.plugin

import com.jtm.minecraft.core.domain.dto.SuggestionDto
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("suggestions")
data class Suggestion(val id: UUID = UUID.randomUUID(), val pluginId: UUID, val accountId: UUID, var comment: String, var likes: MutableList<UUID> = mutableListOf(), var dislikes: MutableList<UUID> = mutableListOf(), var edited: Long = System.currentTimeMillis(), val created: Long = System.currentTimeMillis()) {

    constructor(accountId: UUID, dto: SuggestionDto): this( pluginId = dto.pluginId, accountId = accountId, comment = dto.comment)

    fun updateComment(comment: String): Suggestion {
        this.comment = comment
        this.edited = System.currentTimeMillis()
        return this
    }

    fun addLike(account: UUID): Suggestion {
        if (this.dislikes.contains(account)) this.dislikes.remove(account)
        this.likes.add(account)
        return this
    }

    fun addDislike(account: UUID): Suggestion {
        if (this.likes.contains(account)) this.likes.remove(account)
        this.dislikes.add(account)
        return this
    }
}
