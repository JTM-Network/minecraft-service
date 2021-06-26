package com.jtm.minecraft.core.domain.entity

import com.jtm.minecraft.core.domain.dto.PluginDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("plugins")
data class Plugin(
    @Id val id: UUID,
    var name: String,
    var description: String,
    var version: String?,
    val createdTime: Long,
    var lastUpdated: Long
) {
    fun update(dto: PluginDto) {
        this.name = dto.name
        this.description = dto.description
    }
}
