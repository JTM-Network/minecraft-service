package com.jtm.minecraft.core.usecase.repository.plugin

import com.jtm.minecraft.core.domain.entity.plugin.PluginVersion
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface PluginVersionRepository: ReactiveMongoRepository<PluginVersion, UUID> {

    fun findByPluginId(id: UUID): Flux<PluginVersion>

    fun findByPluginIdAndVersion(id: UUID, version: String): Mono<PluginVersion>

    fun findByPluginName(name: String): Flux<PluginVersion>
}