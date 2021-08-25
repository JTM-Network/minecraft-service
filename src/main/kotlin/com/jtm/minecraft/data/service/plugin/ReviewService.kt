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

    /**
     * Insert a review from a user for a plugin they have access to.
     * Uses the authorization header provided to identify the user submitting
     * the review.
     *
     * @param dto - the review to be inserted
     * @param request - the http client request
     * @throws ReviewFound - if the user has already left a review on the plugin
     * @throws InvalidJwtToken - if the {@link HttpHeaders#AUTHORIZATION} returns null
     *                           from request header or if the token is invalid or account id
     *                           is not found in the token given
     * @return the review the user has given
     */
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

    /**
     * Update the rating of a review the user has given on a plugin.
     *
     * @param dto - the review to be updated
     * @param request - the http client request
     * @throws ReviewNotFound - if the user has not left a review on the plugin
     * @throws InvalidJwtToken - if the {@link HttpHeaders#AUTHORIZATION} returns null
     *                           from request header or if the token is invalid or account id
     *                           is not found in the token given
     * @return the updated review
     */
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

    /**
     * Update the comment of a review the user has given.
     *
     * @param dto - the review to be updated
     * @param request - the http client request
     * @throws ReviewNotFound - if the user has not left a review on the plugin
     * @throws InvalidJwtToken - if the {@link HttpHeaders#AUTHORIZATION} returns null
     *                           from request header or if the token is invalid or account id
     *                           is not found in the token given
     * @return the updated review
     */
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

    /**
     * Returns the review by id
     *
     * @param id - the review identifier
     * @throws ReviewNotFound - if the review has not been found
     * @return the review
     */
    fun getReview(id: UUID): Mono<PluginReview> {
        return reviewRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error { ReviewNotFound() } })
    }

    /**
     * Returns the reviews found by plugin id
     *
     * @param pluginId - the plugin identifier
     * @return list of the reviews found under plugin id
     */
    fun getReviewByPlugin(pluginId: UUID): Flux<PluginReview> {
        return reviewRepository.findByPluginId(pluginId)
    }

    /**
     * Returns all the review found
     *
     * @return list of the reviews
     */
    fun getReviews(): Flux<PluginReview> {
        return reviewRepository.findAll()
    }

    /**
     * Removes the review found by identifier
     *
     * @param id - the review identifier
     * @throws ReviewNotFound - if the review has not been found
     * @return the review being removed
     */
    fun removeReview(id: UUID): Mono<PluginReview> {
        return reviewRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error { ReviewNotFound() } })
            .flatMap { reviewRepository.delete(it).thenReturn(it) }
    }
}