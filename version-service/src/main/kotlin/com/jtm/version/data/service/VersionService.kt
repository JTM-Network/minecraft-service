package com.jtm.version.data.service

import com.jtm.version.core.domain.entity.Version
import com.jtm.version.core.domain.exceptions.version.VersionNotFound
import com.jtm.version.core.usecase.repository.VersionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class VersionService @Autowired constructor(private val versionRepository: VersionRepository) {

    /**
     * This will get the version using the version identifier.
     *
     * @param id        the version identifier
     * @return          the version found
     * @see             Version
     * @throws VersionNotFound if the version is not found in the database.
     */
    fun getVersion(id: UUID): Mono<Version> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(VersionNotFound()) })
    }

    fun getPluginVersion(id: UUID, version: String): Mono<Version> {
        return versionRepository.findByPluginIdAndVersion(id, version)
            .switchIfEmpty(Mono.defer { Mono.error(VersionNotFound()) })
    }

    /**
     * This will get the versions found under the plugin identifier
     *
     * @param id        the plugin identifier
     * @return          the versions found
     * @see             Version
     */
    fun getVersionsByPlugin(id: UUID): Flux<Version> {
        return versionRepository.findByPluginId(id)
    }

    fun getLatestVersion(id: UUID): Mono<Version> {
        return versionRepository.findByPluginId(id)
            .switchIfEmpty(Mono.defer { Mono.error(VersionNotFound()) })
            .sort { version, version2 -> version2.compareTo(version) }
            .take(1)
            .next()
    }

    /**
     * This will get all versions found in the database.
     *
     * @return          the versions found
     * @see             Version
     */
    fun getVersions(): Flux<Version> {
        return versionRepository.findAll()
    }

    /**
     * This will delete the version by the identifier.
     *
     * @param id        the version identifier
     * @return          the version deleted.
     * @see             Version
     * @throws VersionNotFound if the version is not found.
     */
    fun deleteVersion(id: UUID): Mono<Version> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(VersionNotFound()) })
            .flatMap { versionRepository.delete(it).thenReturn(it) }
    }
}