package com.jtm.version.data.service

import com.jtm.version.core.domain.dto.VersionDto
import com.jtm.version.core.domain.entity.Version
import com.jtm.version.core.domain.exceptions.filesystem.FileNotFound
import com.jtm.version.core.domain.exceptions.version.VersionFound
import com.jtm.version.core.usecase.file.FileSystemHandler
import com.jtm.version.core.usecase.file.StandardFileSystemHandler
import com.jtm.version.core.usecase.repository.VersionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UploadService @Autowired constructor(private val versionRepository: VersionRepository, @Qualifier("standard") private val fileSystemHandler: FileSystemHandler) {

    fun uploadResource(dto: VersionDto): Mono<Version> {
        return versionRepository.findByPluginIdAndVersion(dto.pluginId, dto.version)
            .flatMap<Version> { Mono.error(VersionFound()) }
            .switchIfEmpty(Mono.defer {
                if (dto.file == null) return@defer Mono.error { FileNotFound() }
                versionRepository.save(Version(dto))
                    .flatMap { version -> fileSystemHandler.save("/${dto.pluginId}", dto.file, "${dto.name}-${dto.version}")
                        .thenReturn(version)
                    }
            })
    }
}