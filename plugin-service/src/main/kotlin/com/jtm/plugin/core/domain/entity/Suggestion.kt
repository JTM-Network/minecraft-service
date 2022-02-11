package com.jtm.plugin.core.domain.entity

import com.fasterxml.jackson.annotation.JsonProperty
import com.jtm.plugin.core.domain.constants.ReviewStatus
import com.jtm.plugin.core.domain.dto.SuggestionDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*
import java.util.concurrent.TimeUnit

@Document("suggestions")
data class Suggestion(@Id val id: UUID = UUID.randomUUID(),
                      val pluginId: UUID,
                      val originalPoster: String,
                      var comment: String,
                      var status: ReviewStatus = ReviewStatus.IN_REVIEW,
                      val upVotes: MutableList<String> = mutableListOf(),
                      val downVotes: MutableList<String> = mutableListOf(),
                      val posted: Long = System.currentTimeMillis()) {

    constructor(poster: String, dto: SuggestionDto): this(pluginId = dto.pluginId, originalPoster = poster, comment = dto.comment)

    fun canPost(): Boolean {
        return System.currentTimeMillis() > (this.posted + TimeUnit.HOURS.toMillis(6))
    }

    fun updateComment(comment: String): Suggestion {
        this.comment = comment
        return this
    }

    fun hasUpVoted(accountId: String): Boolean {
        return this.upVotes.contains(accountId)
    }

    fun hasDownVoted(accountId: String): Boolean {
        return this.downVotes.contains(accountId)
    }

    fun addUpVote(accountId: String): Suggestion {
        if (downVotes.contains(accountId)) downVotes.remove(accountId)
        this.upVotes.add(accountId)
        return this
    }

    fun addDownVote(accountId: String): Suggestion {
        if (upVotes.contains(accountId)) upVotes.remove(accountId)
        this.downVotes.add(accountId)
        return this
    }

    fun isAccepted(): Boolean {
        return this.status == ReviewStatus.ACCEPTED
    }

    fun updateStatus(status: ReviewStatus): Suggestion {
        this.status = status
        return this
    }

    @JsonProperty
    fun upVoteCount(): Int {
        return this.upVotes.size
    }

    @JsonProperty
    fun downVoteCount(): Int {
        return this.downVotes.size
    }
}
