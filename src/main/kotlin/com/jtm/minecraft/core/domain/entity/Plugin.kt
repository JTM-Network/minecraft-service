package com.jtm.minecraft.core.domain.entity

import com.jtm.minecraft.core.domain.dto.PluginDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("plugins")
data class Plugin(
    @Id val id: UUID = UUID.randomUUID(),
    var name: String,
    var description: String,
    var version: String? = null,
    val createdTime: Long = System.currentTimeMillis(),
    var lastUpdated: Long = System.currentTimeMillis()) {

    fun update(dto: PluginDto): Plugin {
        this.name = dto.name
        this.description = dto.description
        this.lastUpdated = System.currentTimeMillis()
        return this
    }
}
