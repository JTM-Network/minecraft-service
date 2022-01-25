package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.PluginVersionDto
import com.jtm.minecraft.core.domain.entity.DownloadLink
import com.jtm.minecraft.core.domain.entity.plugin.PluginVersion
import com.jtm.minecraft.core.domain.exceptions.RemoteAddressInvalid
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionFound
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionNotFound
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileNoAccess
import com.jtm.minecraft.core.domain.model.FolderInfo
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.repository.DownloadLinkRepository
import com.jtm.minecraft.core.usecase.repository.plugin.PluginVersionRepository
import com.jtm.minecraft.data.service.PluginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class VersionService @Autowired constructor(private val pluginService: PluginService,
                                            private val versionRepository: PluginVersionRepository,
                                            private val downloadLinkRepository: DownloadLinkRepository) {

    /**
     * Insert a plugin version file to be downloaded by users. Also updates the plugins
     * the latest version value.
     *
     * @param dto - version values to be inserted
     * @param fileHandler - handler to store file given
     * @return - the plugin version saved
     * @throws VersionFound if a plugin id & version is already found
     */
    fun insertVersion(dto: PluginVersionDto, fileHandler: FileHandler): Mono<PluginVersion> {
        return pluginService.getPlugin(dto.pluginId)
            .flatMap { plugin -> versionRepository.findByPluginIdAndVersion(plugin.id, dto.version)
                .flatMap<PluginVersion?> { Mono.defer { Mono.error { VersionFound() } } }
                .switchIfEmpty(Mono.defer { versionRepository.save(PluginVersion(pluginId = plugin.id, pluginName = plugin.name, version = dto.version, changelog = dto.changelog)) })
            }
            .flatMap { version ->
                getLatest(version.pluginId)
                    .flatMap { latest -> pluginService.updateVersion(latest.pluginId, latest.version)
                    .flatMap { fileHandler.save("/versions/${version.pluginId}", dto.file!!, "${version.pluginName}-${version.version}.jar").thenReturn(latest) }
                }
            }
    }

    /**
     * Updates version values using the version {@link UUID}
     *
     * @param id - the id of the plugin version
     * @param dto - the new values to update the version with
     * @return - the version updated
     * @throws VersionNotFound - if the version is not found by id.
     */
    fun updateVersion(id: UUID, dto: PluginVersionDto): Mono<PluginVersion> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error { VersionNotFound() } })
            .flatMap { versionRepository.save(it.update(dto)) }
    }

    /**
     * Request a URL to be able to download the requested version without direct authentication using
     * Authorization headers, but to be protected using the requesters IP-Address.
     *
     * @param name - name of the plugin
     * @param version - version the user wants to download
     * @param accessService - service to check the accessibility of the users account
     * @param request - the request from http client
     * @return - the download link to be used
     * @throws RemoteAddressInvalid - if the request ip address is null
     * @throws ProfileNoAccess - if the {@param accessService} returns a value
     * @throws VersionNotFound - if plugin id and version provided are not found
     */
    fun downloadVersionRequest(name: String, version: String, accessService: AccessService, request: ServerHttpRequest): Mono<String> {
        val remoteAddress = request.remoteAddress ?: return Mono.error { RemoteAddressInvalid() }
        val ipAddress = remoteAddress.address.hostAddress
        return accessService.hasAccess(name, request)
            .flatMap<String?> { Mono.defer { Mono.error(ProfileNoAccess()) } }
            .switchIfEmpty(Mono.defer { pluginService.getPluginByName(name)
                .flatMap { plugin -> versionRepository.findByPluginIdAndVersion(plugin.id, version)
                    .switchIfEmpty(Mono.defer { Mono.error { VersionNotFound() } })
                    .flatMap { downloadLinkRepository.save(DownloadLink(pluginId = plugin.id, version = version, ipAddress = ipAddress))
                        .map { "https://api.jtm-network.com/mc/download/${it.id}" }
                    }
                }
            })
    }


    /**
     * Return latest version found by plugin id
     *
     * @return the latest plugin version
     */
    fun getLatest(pluginId: UUID): Mono<PluginVersion> {
        return versionRepository.findByPluginId(pluginId).sort { first, second -> first.compareTo(second) }.take(1).next()
    }

    /**
     * Return plugin version found by id
     *
     * @return the plugin version
     * @throws VersionNotFound - if plugin version is not found using the id
     */
    fun getVersion(id: UUID): Mono<PluginVersion> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error { VersionNotFound() } })
    }

    /**
     * Return all plugin versions found by plugin id
     *
     * @return list of plugin versions
     */
    fun getVersionsByPluginId(id: UUID): Flux<PluginVersion> {
        return versionRepository.findByPluginId(id)
    }

    /**
     * Return all plugin version found by name
     *
     * @return list of plugin versions
     */
    fun getVersionsByPluginName(name: String): Flux<PluginVersion> {
        return versionRepository.findByPluginName(name)
    }

    /**
     * Return all plugin versions
     *
     * @return list of plugin versions
     */
    fun getVersions(): Flux<PluginVersion> {
        return versionRepository.findAll()
    }

    /**
     * Remove plugin version using id
     *
     * @return the plugin version that's been removed
     * @throws VersionNotFound if plugin version has not been found
     */
    fun removeVersion(id: UUID): Mono<PluginVersion> {
        return versionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error { VersionNotFound() } })
            .flatMap { versionRepository.delete(it).thenReturn(it) }
    }

    /**
     * Get all the version folders created for each plugin id
     *
     * @return list of folders found in versions folder
     */
    fun getFolderVersions(fileHandler: FileHandler): Flux<FolderInfo> {
        return fileHandler.listFiles("/versions")
            .map { FolderInfo(it.name) }
    }

    /**
     * Remove a version folder using the plugin id e.g. folder name
     *
     * @return the folder that has been removed.
     */
    fun removeFolderVersion(id: UUID, fileHandler: FileHandler): Mono<String> {
        return fileHandler.delete("/versions/${id}")
            .map { it.name }
    }
}