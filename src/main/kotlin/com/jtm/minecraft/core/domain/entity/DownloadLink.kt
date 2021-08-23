package com.jtm.minecraft.core.domain.entity

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import java.util.concurrent.TimeUnit

@Document("download_links")
data class DownloadLink(
    val id: UUID = UUID.randomUUID(),
    val pluginId: UUID,
    val version: String,
    val accountId: UUID,
    var used: Boolean = false,
    val created: Long = System.currentTimeMillis()) {

    fun valid(): Boolean {
        return !used && (System.currentTimeMillis() < (created + TimeUnit.DAYS.toMillis(1)))
    }

    fun invalidate(): DownloadLink {
        this.used = true
        return this
    }
}
