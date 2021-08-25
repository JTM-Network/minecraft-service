package com.jtm.minecraft.core.domain.entity

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import java.util.concurrent.TimeUnit

@Document("download_links")
data class DownloadLink(
    val id: UUID = UUID.randomUUID(),
    val pluginId: UUID,
    val version: String,
    val ipAddress: String,
    val created: Long = System.currentTimeMillis())
