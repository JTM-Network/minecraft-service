package com.jtm.version.data.service

import com.jtm.version.core.domain.dto.DownloadRequestDto
import com.jtm.version.core.domain.entity.DownloadLink
import com.jtm.version.core.domain.exceptions.download.ClientIdNotFound
import com.jtm.version.core.domain.exceptions.download.DownloadLinkNotFound
import com.jtm.version.core.domain.exceptions.version.VersionNotFound
import com.jtm.version.core.usecase.auth.ProfileAuthorization
import com.jtm.version.core.usecase.repository.DownloadRepository
import com.jtm.version.core.usecase.repository.VersionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class DownloadRequestService @Autowired constructor(private val downloadRepository: DownloadRepository,
                                                    private val versionRepository: VersionRepository,
                                                    private val authorization: ProfileAuthorization) {

    /**
     * This will request a download link for the plugin version resource
     *
     * @param request       the request made by the client
     * @param dto           the data transfer object containing the plugin identifier & version
     * @return              the identifier of the download link
     * @see                 DownloadLink
     */
    fun requestDownload(request: ServerHttpRequest, dto: DownloadRequestDto): Mono<DownloadLink> {
        val id = request.headers.getFirst("CLIENT_ID") ?: return Mono.error { ClientIdNotFound() }
        return versionRepository.findByPluginIdAndVersion(dto.pluginId, dto.version)
            .switchIfEmpty(Mono.defer { Mono.error(VersionNotFound()) })
            .flatMap { authorization.authorize(id, dto.pluginId)
                .flatMap { downloadRepository.save(DownloadLink(pluginId = dto.pluginId, version = dto.version, clientId = id)) }
            }
    }

    /**
     * This will remove the download request.
     *
     * @param id        the download request identifier
     * @return          the download request removed
     * @see             DownloadLink
     * @throws DownloadLinkNotFound     if the request is not found.
     */
    fun removeDownload(id: UUID): Mono<DownloadLink> {
        return downloadRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(DownloadLinkNotFound()) })
            .flatMap { downloadRepository.delete(it).thenReturn(it) }
    }
}