package com.jtm.version.data.service

import com.jtm.version.core.domain.entity.Version
import com.jtm.version.core.domain.exceptions.VersionNotFound
import com.jtm.version.core.usecase.repository.VersionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class VersionService @Autowired constructor(private val versionRepository: VersionRepository) {

    fun getVersion(id: UUID): Mono<Version> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(VersionNotFound()) })
    }

    fun getVersionsByPlugin(id: UUID): Flux<Version> {
        return versionRepository.findByPluginId(id)
    }

    fun getVersions(): Flux<Version> {
        return versionRepository.findAll()
    }

    fun deleteVersion(id: UUID): Mono<Version> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(VersionNotFound()) })
            .flatMap { versionRepository.delete(it).thenReturn(it) }
    }
}