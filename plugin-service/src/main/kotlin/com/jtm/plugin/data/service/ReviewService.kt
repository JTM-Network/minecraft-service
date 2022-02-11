package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.ReviewDto
import com.jtm.plugin.core.domain.entity.Review
import com.jtm.plugin.core.domain.exception.profile.ClientIdNotFound
import com.jtm.plugin.core.domain.exception.review.OnlyOneReview
import com.jtm.plugin.core.domain.exception.review.ReviewNotFound
import com.jtm.plugin.core.usecase.repository.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class ReviewService @Autowired constructor(private val reviewRepository: ReviewRepository) {

    fun addReview(req: ServerHttpRequest, dto: ReviewDto): Mono<Review> {
        val id = req.headers.getFirst("CLIENT_ID") ?: return Mono.error { ClientIdNotFound() }
        return reviewRepository.findByPluginIdAndPoster(dto.pluginId, id)
            .flatMap<Review> { Mono.error(OnlyOneReview()) }
            .switchIfEmpty(Mono.defer { reviewRepository.save(Review(id, dto)) })
    }

    fun updateRating(req: ServerHttpRequest, id: UUID, dto: ReviewDto): Mono<Review> {
        val clientId = req.headers.getFirst("CLIENT_ID") ?: return Mono.error { ClientIdNotFound() }
        return reviewRepository.findByIdAndPluginIdAndPoster(id, dto.pluginId, clientId)
            .switchIfEmpty(Mono.defer { Mono.error(ReviewNotFound()) })
            .flatMap { reviewRepository.save(it.updateRating(dto.rating)) }
    }

    fun updateComment(req: ServerHttpRequest, id: UUID, dto: ReviewDto): Mono<Review> {
        val clientId = req.headers.getFirst("CLIENT_ID") ?: return Mono.error { ClientIdNotFound() }
        return reviewRepository.findByIdAndPluginIdAndPoster(id, dto.pluginId, clientId)
            .switchIfEmpty(Mono.defer { Mono.error(ReviewNotFound()) })
            .flatMap { reviewRepository.save(it.updateComment(dto.comment)) }
    }

    fun getReview(id: UUID): Mono<Review> {
        return reviewRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ReviewNotFound()) })
    }

    fun getReviewsByPluginId(pluginId: UUID): Flux<Review> {
        return reviewRepository.findByPluginId(pluginId)
    }

    fun getReviewsByPoster(req: ServerHttpRequest): Flux<Review> {
        val id = req.headers.getFirst("CLIENT_ID") ?: return Flux.error(ClientIdNotFound())
        return reviewRepository.findByPoster(id)
    }

    fun getReviewsByPosterId(poster: String): Flux<Review> {
        return reviewRepository.findByPoster(poster)
    }

    fun getReviews(): Flux<Review> {
        return reviewRepository.findAll()
    }

    fun removeReview(id: UUID): Mono<Review> {
        return reviewRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ReviewNotFound()) })
            .flatMap { reviewRepository.delete(it).thenReturn(it) }
    }
}