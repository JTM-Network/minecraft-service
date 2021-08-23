package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionFileNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.repository.DownloadLinkRepository
import com.jtm.minecraft.core.usecase.repository.plugin.PluginVersionRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class DownloadService @Autowired constructor(private val linkRepository: DownloadLinkRepository,
                                             private val versionRepository: PluginVersionRepository,
                                             private val fileHandler: FileHandler) {

    fun downloadVersion(response: ServerHttpResponse, id: UUID): Mono<Resource> {
        return linkRepository.findById(id)
            .flatMap { link -> versionRepository.findByPluginIdAndVersion(link.pluginId, link.version)
                .flatMap { fileHandler.fetch("/versions/${link.pluginId.toString()}/${it.pluginName}-${link.version}.jar")
                    .flatMap { file ->
                        if (!file.exists()) return@flatMap Mono.error { VersionFileNotFound() }
                        response.headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${file.name}")
                        return@flatMap Mono.just(FileSystemResource(file))
                    }
                    .doOnSuccess { linkRepository.delete(link) }
                }
            }
    }
}