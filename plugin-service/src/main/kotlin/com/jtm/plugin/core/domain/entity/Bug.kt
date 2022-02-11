package com.jtm.plugin.core.domain.entity

import com.jtm.plugin.core.domain.constants.BugStatus
import com.jtm.plugin.core.domain.constants.ReviewStatus
import com.jtm.plugin.core.domain.dto.BugDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import java.util.concurrent.TimeUnit

@Document("bugs")
data class Bug(@Id val id: UUID = UUID.randomUUID(),
               val pluginId: UUID,
               val poster: String,
               val serverVersion: String,
               val pluginVersion: String,
               val recreateComment: String,
               val happensComment: String,
               var reviewStatus: ReviewStatus = ReviewStatus.IN_REVIEW,
               var bugStatus: BugStatus = BugStatus.OPEN,
               var posted: Long = System.currentTimeMillis()) {

    constructor(poster: String, dto: BugDto): this(poster = poster, pluginId = dto.pluginId, serverVersion = dto.serverVersion, pluginVersion = dto.serverVersion, recreateComment = dto.recreateComment, happensComment = dto.happensComment)

    fun canPost(): Boolean {
        return System.currentTimeMillis() > (this.posted + TimeUnit.HOURS.toMillis(6))
    }
}
