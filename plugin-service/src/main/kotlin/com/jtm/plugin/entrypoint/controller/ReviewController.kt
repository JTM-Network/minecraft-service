package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.ReviewDto
import com.jtm.plugin.core.domain.entity.Review
import com.jtm.plugin.data.service.ReviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/review")
class ReviewController @Autowired constructor(private val reviewService: ReviewService) {

    @PostMapping
    fun postReview(req: ServerHttpRequest, @RequestBody dto: ReviewDto): Mono<Review> {
        return reviewService.addReview(req, dto)
    }

    @PutMapping("/{id}/rating")
    fun putRating(req: ServerHttpRequest, @PathVariable id: UUID, @RequestBody dto: ReviewDto): Mono<Review> {
        return reviewService.updateRating(req, id, dto)
    }

    @PutMapping("/{id}/comment")
    fun putComment(req: ServerHttpRequest, @PathVariable id: UUID, @RequestBody dto: ReviewDto): Mono<Review> {
        return reviewService.updateComment(req, id, dto)
    }

    @GetMapping("/{id}")
    fun getReview(@PathVariable id: UUID): Mono<Review> {
        return reviewService.getReview(id)
    }

    @GetMapping("/plugin/{pluginId}")
    fun getReviewsByPluginId(@PathVariable pluginId: UUID): Flux<Review> {
        return reviewService.getReviewsByPluginId(pluginId)
    }

    @GetMapping("/rating/{pluginId}")
    fun getRatingByPlugin(@PathVariable pluginId: UUID): Mono<Int> {
        return reviewService.getRatingByPlugin(pluginId)
    }

    @GetMapping("/poster")
    fun getReviewsByPoster(req: ServerHttpRequest): Flux<Review> {
        return reviewService.getReviewsByPoster(req)
    }

    @GetMapping("/poster/{poster}")
    fun getReviewsByPosterId(@PathVariable poster: String): Flux<Review> {
        return reviewService.getReviewsByPosterId(poster)
    }

    @GetMapping("/all")
    fun getReviews(): Flux<Review> {
        return reviewService.getReviews()
    }

    @DeleteMapping("/{id}")
    fun deleteReview(@PathVariable id: UUID): Mono<Review> {
        return reviewService.removeReview(id)
    }
}