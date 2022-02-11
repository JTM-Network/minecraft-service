package com.jtm.plugin.core.usecase.repository

import com.jtm.plugin.core.domain.entity.Bug
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.*

@Repository
interface BugRepository: ReactiveMongoRepository<Bug, UUID> {

    fun findByPluginId(pluginId: UUID): Flux<Bug>

    fun findByPoster(poster: String): Flux<Bug>

    fun findByPluginIdAndPoster(pluginId: UUID, poster: String): Flux<Bug>
}