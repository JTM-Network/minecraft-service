package com.jtm.plugin.core.usecase.repository

import com.jtm.plugin.core.domain.entity.Suggestion
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface SuggestionRepository: ReactiveMongoRepository<Suggestion, UUID> {

    fun findByIdAndPluginIdAndOriginalPoster(id: UUID, pluginId: UUID, originalPoster: String): Mono<Suggestion>

    fun findByPluginIdAndOriginalPoster(pluginId: UUID, originalPoster: String): Flux<Suggestion>

    fun findByOriginalPoster(originalPoster: String): Flux<Suggestion>

    fun findByPluginId(id: UUID): Flux<Suggestion>
}