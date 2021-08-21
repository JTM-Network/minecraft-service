package com.jtm.minecraft.core.usecase.repository.plugin

import com.jtm.minecraft.core.domain.entity.plugin.PluginReview
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface PluginReviewRepository: ReactiveMongoRepository<PluginReview, UUID> {

    fun findByAccountId(accountId: UUID): Flux<PluginReview>

    fun findByAccountIdAndPluginId(accountId: UUID, pluginId: UUID): Mono<PluginReview>

    fun findByPluginId(pluginId: UUID): Flux<PluginReview>
}