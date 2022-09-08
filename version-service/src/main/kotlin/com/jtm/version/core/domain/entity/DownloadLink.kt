package com.jtm.version.core.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import java.util.concurrent.TimeUnit

@Document("download_links")
data class DownloadLink(@Id val id: UUID = UUID.randomUUID(), val pluginId: UUID, val version: String, val clientId: String, var available: Boolean = true, val endTime: Long = (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)), val createdTime: Long = System.currentTimeMillis()) {

    fun canDownload(): Boolean {
        return System.currentTimeMillis() < endTime && available
    }

    fun download(): DownloadLink {
        this.available = false
        return this
    }
}
