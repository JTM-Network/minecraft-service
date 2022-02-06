package com.jtm.version.data.service

import com.jtm.version.core.domain.entity.DownloadLink
import com.jtm.version.core.domain.exceptions.download.DownloadLinkNotFound
import com.jtm.version.core.domain.exceptions.version.VersionNotFound
import com.jtm.version.core.domain.exceptions.filesystem.FileNotFound
import com.jtm.version.core.usecase.file.FileSystemHandler
import com.jtm.version.core.usecase.repository.DownloadRepository
import com.jtm.version.core.usecase.repository.VersionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class DownloadService @Autowired constructor(private val downloadRepository: DownloadRepository,
                                             private val versionRepository: VersionRepository,
                                             private val fileSystemHandler: FileSystemHandler) {

    /**
     * This will check for the download request made and return the resource from the request.
     *
     * @param id        this is the request identifier
     * @return          the version resource file
     * @see             Resource
     * @throws DownloadLinkNotFound     if the request is not found.
     * @throws VersionNotFound          if the version is not found.
     * @throws FileNotFound             if the file is not found
     */
    fun getDownload(response: ServerHttpResponse, id: UUID): Mono<Resource> {
        return downloadRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(DownloadLinkNotFound()) })
            .flatMap { link -> versionRepository.findByPluginIdAndVersion(link.pluginId, link.version)
                .switchIfEmpty(Mono.defer { Mono.error(VersionNotFound()) })
                .flatMap { version -> fileSystemHandler.fetch("/${version.pluginId}/${version.pluginName}-${version.version}.jar")
                    .map { file ->
                        response.headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${file.name}")
                        FileSystemResource(file)
                    }
                    .doOnSuccess { downloadRepository.save(link.download()) }
                    .doOnSuccess { versionRepository.save(version.addDownload()) }
                }
            }
    }
}