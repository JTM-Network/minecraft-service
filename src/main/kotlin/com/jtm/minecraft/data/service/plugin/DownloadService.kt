package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.exceptions.InvalidUserDownload
import com.jtm.minecraft.core.domain.exceptions.RemoteAddressInvalid
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionFileNotFound
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.repository.DownloadLinkRepository
import com.jtm.minecraft.core.usecase.repository.plugin.PluginVersionRepository
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

    /**
     * Returns the plugin version resource to be used by users. Using the
     * {@link DownloadLink} stored it will match the user's IP-Address to be
     * able to complete the download. Once download is finished will remove
     * the download link stored.
     *
     * @param request - the client request
     * @param response - the server side response to the user
     * @param id - the identifier for the {@link DownloadLink}
     *              when requested at endpoint {@link VersionController#getDownloadRequest}
     * @throws RemoteAddressInvalid - if the request ip address is null
     * @throws InvalidUserDownload - if the request ip address does not match the download link
     *                               ip address.
     * @throws VersionFileNotFound - if the file has not been saved on the disk
     * @return the file system resource found
     */
    fun downloadVersion(request: ServerHttpRequest, response: ServerHttpResponse, id: UUID): Mono<Resource> {
        val remoteAddress = request.remoteAddress ?: return Mono.error { RemoteAddressInvalid() }
        val ipAddress = remoteAddress.address.hostAddress
        return linkRepository.findById(id)
            .flatMap { link ->
                if (link.ipAddress != ipAddress) return@flatMap Mono.error { InvalidUserDownload() }
                versionRepository.findByPluginIdAndVersion(link.pluginId, link.version)
                    .flatMap { fileHandler.fetch("/versions/${link.pluginId}/${it.pluginName}-${link.version}.jar")
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