package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.PluginReviewDto
import com.jtm.minecraft.core.domain.entity.plugin.PluginReview
import com.jtm.minecraft.core.domain.exceptions.plugin.review.ReviewFound
import com.jtm.minecraft.core.domain.exceptions.plugin.review.ReviewNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.repository.plugin.PluginReviewRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.PluginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class ReviewService @Autowired constructor(private val pluginService: PluginService,
                                           private val reviewRepository: PluginReviewRepository,
                                           private val tokenProvider: AccountTokenProvider) {

    fun insertReview(dto: PluginReviewDto, request: ServerHttpRequest): Mono<PluginReview> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        return pluginService.getPlugin(dto.pluginId)
            .flatMap { plugin -> reviewRepository.findByAccountIdAndPluginId(accountId, plugin.id)
                .flatMap<PluginReview?> { Mono.defer { Mono.error { ReviewFound() } } }
                .switchIfEmpty(Mono.defer { reviewRepository.save(PluginReview(accountId = accountId, pluginId = plugin.id, rating = dto.rating, comment = dto.comment)) })
            }
    }

    fun updateReviewRating(dto: PluginReviewDto, request: ServerHttpRequest): Mono<PluginReview> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        return pluginService.getPlugin(dto.pluginId)
            .flatMap { plugin -> reviewRepository.findByAccountIdAndPluginId(accountId, plugin.id)
                .switchIfEmpty(Mono.defer { Mono.error { ReviewNotFound() } })
                .flatMap { reviewRepository.save(it.updateRating(dto.rating)) }
            }
    }

    fun updateReviewComment(dto: PluginReviewDto, request: ServerHttpRequest): Mono<PluginReview> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        return pluginService.getPlugin(accountId)
            .flatMap { plugin -> reviewRepository.findByAccountIdAndPluginId(accountId, plugin.id)
                .switchIfEmpty(Mono.defer { Mono.error { ReviewNotFound() }})
                .flatMap { reviewRepository.save(it.updateComment(dto.comment)) }
            }
    }

    fun getReview(id: UUID): Mono<PluginReview> {
        return reviewRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error { ReviewNotFound() } })
    }

    fun getReviewByPlugin(pluginId: UUID): Flux<PluginReview> {
        return reviewRepository.findByPluginId(pluginId)
    }

    fun getReviews(): Flux<PluginReview> {
        return reviewRepository.findAll()
    }

    fun removeReview(id: UUID): Mono<PluginReview> {
        return reviewRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error { ReviewNotFound() } })
            .flatMap { reviewRepository.delete(it).thenReturn(it) }
    }
}