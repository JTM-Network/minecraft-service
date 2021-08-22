package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.PluginVersionDto
import com.jtm.minecraft.core.domain.entity.plugin.PluginVersion
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionFound
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionNotFound
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.repository.plugin.PluginVersionRepository
import com.jtm.minecraft.data.service.PluginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class VersionService @Autowired constructor(private val pluginService: PluginService,
                                            private val fileHandler: FileHandler,
                                            private val versionRepository: PluginVersionRepository) {

    fun insertVersion(dto: PluginVersionDto): Mono<PluginVersion> {
        return pluginService.getPlugin(dto.pluginId)
            .flatMap { plugin -> versionRepository.findByPluginIdAndVersion(plugin.id, dto.version)
                .flatMap<PluginVersion?> { Mono.defer { Mono.error { VersionFound() } } }
                .switchIfEmpty(Mono.defer { versionRepository.save(PluginVersion(pluginId = plugin.id, pluginName = plugin.name, version = dto.version, changelog = dto.changelog)) })
            }
            .doOnSuccess { fileHandler.save("/${it.pluginName}", dto.file) }
    }

    fun updateVersion(dto: PluginVersionDto): Mono<PluginVersion> {
        return pluginService.getPlugin(dto.pluginId)
            .flatMap { plugin -> versionRepository.findByPluginIdAndVersion(plugin.id, dto.version)
                .switchIfEmpty(Mono.defer { Mono.error { VersionNotFound() } })
                .flatMap { version -> versionRepository.save(version.update(dto)) }
            }
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
}