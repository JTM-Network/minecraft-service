package com.jtm.plugin.core.usecase.repository

import com.jtm.plugin.core.domain.entity.Review
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface ReviewRepository: ReactiveMongoRepository<Review, UUID> {

    fun findByIdAndPluginIdAndPoster(id: UUID, pluginId: UUID, poster: String): Mono<Review>

    fun findByPluginIdAndPoster(pluginId: UUID, poster: String): Mono<Review>

    fun findByPluginId(pluginId: UUID): Flux<Review>

    fun findByPoster(poster: String): Flux<Review>
}