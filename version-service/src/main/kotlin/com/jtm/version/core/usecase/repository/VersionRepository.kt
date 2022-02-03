package com.jtm.version.core.usecase.repository

import com.jtm.version.core.domain.entity.Version
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface VersionRepository: ReactiveMongoRepository<Version, UUID> {

    fun findByPluginId(id: UUID): Flux<Version>

    fun findByPluginIdAndVersion(id: UUID, version: String): Mono<Version>
}