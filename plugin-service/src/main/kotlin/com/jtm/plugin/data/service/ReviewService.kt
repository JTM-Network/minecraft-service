package com.jtm.plugin.data.service

import com.google.gson.GsonBuilder
import com.jtm.plugin.core.domain.dto.ReviewDto
import com.jtm.plugin.core.domain.entity.Review
import com.jtm.plugin.core.domain.exception.BasicInfoNotFound
import com.jtm.plugin.core.domain.exception.profile.ClientIdNotFound
import com.jtm.plugin.core.domain.exception.review.OnlyOneReview
import com.jtm.plugin.core.domain.exception.review.ReviewNotFound
import com.jtm.plugin.core.domain.model.BasicInfo
import com.jtm.plugin.core.usecase.repository.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.stream.Collectors
import kotlin.math.roundToInt

@Service
class ReviewService @Autowired constructor(private val reviewRepository: ReviewRepository) {

    private val gson = GsonBuilder().create()

    /**
     * Allow a user to post a review for a plugin.
     *
     * @param req       the http request.
     * @param dto       the review data transfer object.
     * @return          the saved review.
     * @throws          ClientIdNotFound if the header "CLIENT_ID" returns a null or blank value
     * @throws          OnlyOneReview if the user has already reviewed this plugin.
     * @see             Review
     */
    fun addReview(req: ServerHttpRequest, dto: ReviewDto): Mono<Review> {
        val id = req.headers.getFirst("CLIENT_ID")
        if (id.isNullOrBlank()) return Mono.error { ClientIdNotFound() }
        val info = req.headers.getFirst("BASIC_INFO")
        if (info.isNullOrBlank()) return Mono.error { BasicInfoNotFound() }
        val basic: BasicInfo = gson.fromJson(info, BasicInfo::class.java)
        return reviewRepository.findByPluginIdAndPoster(dto.pluginId, id)
            .flatMap<Review> { Mono.error(OnlyOneReview()) }
            .switchIfEmpty(Mono.defer { reviewRepository.save(Review(id, basic.username ?: "", basic.picture ?: "", dto)) })
    }

    /**
     * Allow user to update the review rating.
     *
     * @param req       the http request.
     * @param id        the review identifier.
     * @param dto       the review data transfer object.
     * @return          the updated review.
     * @throws          ClientIdNotFound if the header "CLIENT_ID" returns a null or blank value
     * @throws          ReviewNotFound if the review was not found using the identifier.
     * @see             Review
     */
    fun updateRating(req: ServerHttpRequest, id: UUID, dto: ReviewDto): Mono<Review> {
        val clientId = req.headers.getFirst("CLIENT_ID")
        if (clientId.isNullOrBlank()) return Mono.error { ClientIdNotFound() }
        return reviewRepository.findByIdAndPluginIdAndPoster(id, dto.pluginId, clientId)
            .switchIfEmpty(Mono.defer { Mono.error(ReviewNotFound()) })
            .flatMap { reviewRepository.save(it.updateRating(dto.rating)) }
    }

    /**
     * Allow user to update the review comment.
     *
     * @param req       the http request.
     * @param id        the review identifier.
     * @param dto       the review data transfer object.
     * @return          the updated review.
     * @throws          ClientIdNotFound if the header "CLIENT_ID" returns a null or blank value.
     * @throws          ReviewNotFound if the review was not found using the identifier.
     * @see             Review
     */
    fun updateComment(req: ServerHttpRequest, id: UUID, dto: ReviewDto): Mono<Review> {
        val clientId = req.headers.getFirst("CLIENT_ID")
        if (clientId.isNullOrBlank()) return Mono.error { ClientIdNotFound() }
        return reviewRepository.findByIdAndPluginIdAndPoster(id, dto.pluginId, clientId)
            .switchIfEmpty(Mono.defer { Mono.error(ReviewNotFound()) })
            .flatMap { reviewRepository.save(it.updateComment(dto.comment)) }
    }

    /**
     * Get review by identifier.
     *
     * @param id        the identifier
     * @return          the review found.
     * @throws          ReviewNotFound if the review was not found.
     * @see             Review
     */
    fun getReview(id: UUID): Mono<Review> {
        return reviewRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ReviewNotFound()) })
    }

    /**
     * Get reviews by the plugin identifier.
     *
     * @param pluginId  the plugin identifier.
     * @return          the list of reviews found.
     * @see             Review
     */
    fun getReviewsByPluginId(pluginId: UUID): Flux<Review> {
        return reviewRepository.findByPluginId(pluginId)
    }

    /**
     * Get average rating of a plugin.
     *
     * @param pluginId  the plugin identifier
     * @return          the integer average of the plugin reviews.
     */
    fun getRatingByPlugin(pluginId: UUID): Mono<Int> {
        return reviewRepository.findByPluginId(pluginId)
            .collect(Collectors.averagingDouble(Review::rating))
            .map { it.roundToInt() }
    }

    /**
     * Get reviews by the user http request.
     *
     * @param req       the http request.
     * @return          the list of reviews found under the user.
     * @see             Review
     */
    fun getReviewsByPoster(req: ServerHttpRequest): Flux<Review> {
        val id = req.headers.getFirst("CLIENT_ID") ?: return Flux.error(ClientIdNotFound())
        return reviewRepository.findByPoster(id)
    }

    /**
     * Get reviews by user id.
     *
     * @param poster    the user id.
     * @return          the list of reviews under the user
     * @see             Review
     */
    fun getReviewsByPosterId(poster: String): Flux<Review> {
        return reviewRepository.findByPoster(poster)
    }

    /**
     * Get the list of reviews.
     *
     * @return          the list of reviews.
     */
    fun getReviews(): Flux<Review> {
        return reviewRepository.findAll()
    }

    /**
     * Remove a review using the identifier.
     *
     * @param id        the identifier.
     * @return          the deleted review.
     * @see             Review
     */
    fun removeReview(id: UUID): Mono<Review> {
        return reviewRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ReviewNotFound()) })
            .flatMap { reviewRepository.delete(it).thenReturn(it) }
    }
}