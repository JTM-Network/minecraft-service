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

//    override fun compareTo(other: Version): Int {
//        val thisParts: List<String> = version.split("\\.")
//        val thatParts: List<String> = other.version.split("\\.")
//        val length = thisParts.size.coerceAtLeast(thatParts.size)
//        for (i in 0 until length) {
//            val thisPart = if (i < thisParts.size) thisParts[i].toInt() else 0
//            val thatPart = if (i < thatParts.size) thatParts[i].toInt() else 0
//            if (thisPart < thatPart) return -1
//            if (thisPart > thatPart) return 1
//        }
//
//        return 0
//    }

    override fun compareTo(other: Version): Int {
        val current = DefaultArtifactVersion(version)
        val that = DefaultArtifactVersion(other.version)
        return current.compareTo(that)
    }
}
