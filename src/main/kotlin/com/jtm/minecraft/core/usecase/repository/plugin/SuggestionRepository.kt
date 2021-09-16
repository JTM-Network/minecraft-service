package com.jtm.minecraft.core.usecase.repository.plugin

import com.jtm.minecraft.core.domain.entity.plugin.Suggestion
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface SuggestionRepository: ReactiveMongoRepository<Suggestion, UUID> {

    fun findByPluginId(pluginId: UUID): Flux<Suggestion>

    fun findByAccountId(accountId: UUID): Flux<Suggestion>

    fun findByAccountIdAndPluginId(accountId: UUID, pluginId: UUID): Mono<Suggestion>
}