package com.jtm.version.core.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("versions")
data class Version(@Id val id: UUID = UUID.randomUUID(), val pluginId: UUID, var version: String, var updatedTime: Long = System.currentTimeMillis(), val addedTime: Long = System.currentTimeMillis()) {

    fun updateVersion(version: String): Version {
        this.version = version
        this.updatedTime = System.currentTimeMillis()
        return this
    }
}
