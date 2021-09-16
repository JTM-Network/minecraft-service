package com.jtm.minecraft.core.domain.entity.plugin

import com.jtm.minecraft.core.domain.dto.BugDto
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("bugs")
data class Bug(val id: UUID = UUID.randomUUID(), val pluginId: UUID, val accountId: UUID, var comment: String, val created: Long = System.currentTimeMillis()) {

    constructor(accountId: UUID, dto: BugDto): this(accountId = accountId, pluginId = dto.pluginId, comment = dto.comment)

    fun updateComment(comment: String): Bug {
        this.comment = comment
        return this
    }
}