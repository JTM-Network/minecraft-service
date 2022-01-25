package com.jtm.minecraft.core.usecase.repository.plugin

import com.jtm.minecraft.core.domain.entity.plugin.Bug
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

interface BugRepository: ReactiveMongoRepository<Bug, UUID> {

    fun findByPluginId(pluginId: UUID): Flux<Bug>

    fun findByAccountId(accountId: UUID): Flux<Bug>

    fun findByPluginIdAndAccountId(pluginId: UUID, accountId: UUID): Mono<Bug>
}