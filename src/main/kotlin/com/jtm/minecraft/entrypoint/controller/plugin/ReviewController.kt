package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.dto.PluginReviewDto
import com.jtm.minecraft.core.domain.entity.plugin.PluginReview
import com.jtm.minecraft.data.service.plugin.ReviewService
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
    fun postReview(@RequestBody dto: PluginReviewDto, request: ServerHttpRequest): Mono<PluginReview> {
        return reviewService.insertReview(dto, request)
    }

    @PutMapping("/rating")
    fun putReviewRating(@RequestBody dto: PluginReviewDto, request: ServerHttpRequest): Mono<PluginReview> {
        return reviewService.updateReviewRating(dto, request)
    }

    @PutMapping("/comment")
    fun putReviewComment(@RequestBody dto: PluginReviewDto, request: ServerHttpRequest): Mono<PluginReview> {
        return reviewService.updateReviewComment(dto, request)
    }

    @GetMapping("/{id}")
    fun getReview(@PathVariable id: UUID): Mono<PluginReview> {
        return reviewService.getReview(id)
    }

    @GetMapping("/plugin/{pluginId}")
    fun getReviewByPluginId(@PathVariable pluginId: UUID): Flux<PluginReview> {
        return reviewService.getReviewByPlugin(pluginId)
    }

    @GetMapping("/all")
    fun getReviews(): Flux<PluginReview> {
        return reviewService.getReviews()
    }

    @DeleteMapping("/{id}")
    fun deleteReview(@PathVariable id: UUID): Mono<PluginReview> {
        return reviewService.removeReview(id)
    }
}