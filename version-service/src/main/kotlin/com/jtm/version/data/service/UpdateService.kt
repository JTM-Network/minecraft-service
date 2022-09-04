package com.jtm.version.data.service

import com.jtm.version.core.domain.dto.UpdateDto
import com.jtm.version.core.domain.entity.Version
import com.jtm.version.core.domain.exceptions.version.VersionNotFound
import com.jtm.version.core.usecase.file.FileSystemHandler
import com.jtm.version.core.usecase.repository.VersionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class UpdateService @Autowired constructor(private val versionRepository: VersionRepository, private val fileHandler: FileSystemHandler) {

    fun updateVersion(id: UUID, dto: UpdateDto): Mono<Version> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(VersionNotFound()) })
            .flatMap { version -> fileHandler.updateFileName("/${version.pluginId}/${version.pluginName}-${version.version}.jar", "${version.pluginName}-${dto.version}.jar")
                .thenReturn(version)
                .flatMap { versionRepository.save(it.updateVersion(dto.version)) }
            }
    }

    fun updateChangelog(id: UUID, dto: UpdateDto): Mono<Version> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(VersionNotFound()) })
            .flatMap { versionRepository.save(it.updateChangelog(dto.changelog)) }
    }
}