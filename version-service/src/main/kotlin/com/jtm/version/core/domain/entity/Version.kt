package com.jtm.version.core.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("versions")
data class Version(@Id val id: UUID = UUID.randomUUID(), val pluginId: UUID, val pluginName: String, var version: String, var changelog: String, var downloads: Int = 0, var updatedTime: Long = System.currentTimeMillis(), val addedTime: Long = System.currentTimeMillis()) {

    fun updateVersion(version: String): Version {
        this.version = version
        this.updatedTime = System.currentTimeMillis()
        return this
    }

    fun updateChangelog(changelog: String): Version {
        this.changelog = changelog
        this.updatedTime = System.currentTimeMillis()
        return this
    }

    fun addDownload(): Version {
        this.downloads++
        return this
    }
}
