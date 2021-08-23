package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.PluginVersionDto
import com.jtm.minecraft.core.domain.entity.DownloadLink
import com.jtm.minecraft.core.domain.entity.plugin.PluginVersion
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionFound
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionNotFound
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileNoAccess
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.domain.model.FolderInfo
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.repository.DownloadLinkRepository
import com.jtm.minecraft.core.usecase.repository.PluginRepository
import com.jtm.minecraft.core.usecase.repository.plugin.PluginVersionRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.PluginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class VersionService @Autowired constructor(private val pluginService: PluginService,
                                            private val pluginRepository: PluginRepository,
                                            private val versionRepository: PluginVersionRepository,
                                            private val downloadLinkRepository: DownloadLinkRepository) {

    fun insertVersion(dto: PluginVersionDto, fileHandler: FileHandler): Mono<PluginVersion> {
        return pluginService.getPlugin(dto.pluginId)
            .flatMap { plugin -> versionRepository.findByPluginIdAndVersion(plugin.id, dto.version)
                .flatMap<PluginVersion?> { Mono.defer { Mono.error { VersionFound() } } }
                .switchIfEmpty(Mono.defer { versionRepository.save(PluginVersion(pluginId = plugin.id, pluginName = plugin.name, version = dto.version, changelog = dto.changelog)) })
            }
            .flatMap { version ->
                getLatest(version.pluginId).flatMap { latest -> pluginService.updateVersion(latest.pluginId, latest.version)
                    .flatMap { fileHandler.save("/versions/${version.pluginId.toString()}", dto.file!!, "${version.pluginName}-${version.version}.jar").thenReturn(latest) }
                }
            }
    }

    fun updateVersion(dto: PluginVersionDto): Mono<PluginVersion> {
        return pluginService.getPlugin(dto.pluginId)
            .flatMap { plugin -> versionRepository.findByPluginIdAndVersion(plugin.id, dto.version)
                .switchIfEmpty(Mono.defer { Mono.error { VersionNotFound() } })
                .flatMap { version -> versionRepository.save(version.update(dto)) }
            }
    }

    fun downloadVersionRequest(name: String, version: String, accessService: AccessService, tokenProvider: AccountTokenProvider, request: ServerHttpRequest): Mono<String> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        return accessService.hasAccess(name, request)
            .flatMap<String?> { Mono.defer { Mono.error(ProfileNoAccess()) } }
            .switchIfEmpty(Mono.defer { pluginService.getPluginByName(name)
                .flatMap { plugin -> versionRepository.findByPluginIdAndVersion(plugin.id, version)
                    .switchIfEmpty(Mono.defer { Mono.error { VersionNotFound() } })
                    .flatMap { downloadLinkRepository.save(DownloadLink(pluginId = plugin.id, version = version, accountId = accountId))
                        .map { "https://api.jtm-network.com/mc/download/${it.id}" }
                    }
                }
            })
    }

    fun getLatest(pluginId: UUID): Mono<PluginVersion> {
        return versionRepository.findByPluginId(pluginId).sort { first, second -> first.compareTo(second) }.take(1).next()
    }

    fun getVersion(id: UUID): Mono<PluginVersion> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error { VersionNotFound() } })
    }

    fun getVersionsByPluginId(id: UUID): Flux<PluginVersion> {
        return versionRepository.findByPluginId(id)
    }

    fun getVersionsByPluginName(name: String): Flux<PluginVersion> {
        return versionRepository.findByPluginName(name)
    }

    fun getVersions(): Flux<PluginVersion> {
        return versionRepository.findAll()
    }

    fun removeVersion(id: UUID): Mono<PluginVersion> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error { VersionNotFound() } })
            .flatMap { versionRepository.delete(it).thenReturn(it) }
    }

    fun getFolderVersions(fileHandler: FileHandler): Flux<FolderInfo> {
        return fileHandler.listFiles("/versions")
            .map { FolderInfo(it.name) }
    }

    fun removeFolderVersion(id: UUID, fileHandler: FileHandler): Mono<String> {
        return fileHandler.delete(id.toString())
            .map { it.name }
    }
}