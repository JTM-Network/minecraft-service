package com.jtm.version.core.domain.entity

import com.jtm.version.core.domain.dto.VersionDto
import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*


@Document("versions")
data class Version(@Id val id: UUID = UUID.randomUUID(), val pluginId: UUID, val pluginName: String, var version: String, var changelog: String, var downloads: Int = 0, var updatedTime: Long = System.currentTimeMillis(), val addedTime: Long = System.currentTimeMillis()): Comparable<Version> {

    constructor(dto: VersionDto): this(pluginId = dto.pluginId, pluginName = dto.name, version = dto.version, changelog = dto.changelog)

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

    override fun compareTo(other: Version): Int {
        val current = DefaultArtifactVersion(version)
        val that = DefaultArtifactVersion(other.version)
        return current.compareTo(that)
    }
}
