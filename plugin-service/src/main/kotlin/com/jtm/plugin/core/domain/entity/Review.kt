package com.jtm.plugin.core.domain.entity

import com.jtm.plugin.core.domain.dto.ReviewDto
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("reviews")
data class Review(@Id val id: UUID = UUID.randomUUID(),
                  val pluginId: UUID,
                  val poster: String,
                  val poster_username: String,
                  val poster_picture: String,
                  var rating: Double,
                  var comment: String,
                  var updated: Long = System.currentTimeMillis(),
                  val posted: Long = System.currentTimeMillis()) {

    constructor(poster: String, poster_username: String, poster_picture: String, dto: ReviewDto): this(poster = poster, poster_username = poster_username, poster_picture = poster_picture, pluginId = dto.pluginId, rating = dto.rating, comment = dto.comment)

    fun updateRating(rating: Double): Review {
        this.rating = rating
        return this
    }

    fun updateComment(comment: String): Review {
        this.comment = comment
        return this
    }
}
