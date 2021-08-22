package com.jtm.minecraft.core.domain.entity.plugin

import com.jtm.minecraft.core.domain.dto.PluginVersionDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("plugin_version")
data class PluginVersion(
    @Id val id: UUID = UUID.randomUUID(),
    val pluginId: UUID,
    val pluginName: String,
    var version: String,
    var changelog: String,
    val uploaded: Long = System.currentTimeMillis(),
    var updated: Long = System.currentTimeMillis()): Comparable<PluginVersion> {

    fun update(dto: PluginVersionDto): PluginVersion {
        this.version = dto.version
        this.changelog = dto.changelog
        this.updated = System.currentTimeMillis()
        return this
    }

    override fun compareTo(other: PluginVersion): Int {
        TODO("Not yet implemented")
    }
}