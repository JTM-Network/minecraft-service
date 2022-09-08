package com.jtm.version.data.service

import com.jtm.version.core.domain.dto.DownloadRequestDto
import com.jtm.version.core.domain.entity.DownloadLink
import com.jtm.version.core.domain.entity.Version
import com.jtm.version.core.domain.exceptions.authentication.ProfileUnauthorized
import com.jtm.version.core.domain.exceptions.download.ClientIdNotFound
import com.jtm.version.core.domain.exceptions.download.DownloadLinkNotFound
import com.jtm.version.core.domain.exceptions.version.VersionNotFound
import com.jtm.version.core.usecase.auth.ProfileAuthorization
import com.jtm.version.core.usecase.repository.DownloadRepository
import com.jtm.version.core.usecase.repository.VersionRepository
import com.mongodb.internal.connection.tlschannel.util.Util
import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class DownloadRequestServiceTest {

    private val downloadRepository: DownloadRepository = mock()
    private val versionRepository: VersionRepository = mock()
    private val authorization: ProfileAuthorization = mock()
    private val downloadRequestService = DownloadRequestService(downloadRepository, versionRepository, authorization)

    private val dto = DownloadRequestDto(UUID.randomUUID(), "1.0")
    private val version = Version(pluginId = UUID.randomUUID(), pluginName = "test", version = "1.0", changelog = "Changelog")
    private val downloadLink = DownloadLink(pluginId = version.pluginId, version = version.version, clientId = "id")

    private val request: ServerHttpRequest = mock()
    private val headers: HttpHeaders = mock()

    @Before
    fun setup() {
        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("id")
    }

    @Test
    fun requestDownload_thenClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = downloadRequestService.requestDownload(request, dto)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun requestDownload_thenVersionNotFound() {
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.empty())

        val returned = downloadRequestService.requestDownload(request, dto)

        verify(versionRepository, times(1)).findByPluginIdAndVersion(anyOrNull(), anyString())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .expectError(VersionNotFound::class.java)
            .verify()
    }

    @Test
    fun requestDownload_thenAuthenticationFailed() {
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyOrNull())).thenReturn(Mono.just(version))
        `when`(authorization.authorize(anyString(), anyOrNull())).thenReturn(Mono.error { ProfileUnauthorized() })

        val returned = downloadRequestService.requestDownload(request, dto)

        verify(versionRepository, times(1)).findByPluginIdAndVersion(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .expectError(ProfileUnauthorized::class.java)
            .verify()
    }

    @Test
    fun requestDownload() {
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))
        `when`(authorization.authorize(anyString(), anyOrNull())).thenReturn(Mono.just(true))
        `when`(downloadRepository.save(anyOrNull())).thenReturn(Mono.just(downloadLink))

        val returned = downloadRequestService.requestDownload(request, dto)

        verify(versionRepository, times(1)).findByPluginIdAndVersion(anyOrNull(), anyString())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.id).isEqualTo(downloadLink.id) }
            .verifyComplete()
    }

    @Test
    fun removeDownload_thenNotFound() {
        `when`(downloadRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = downloadRequestService.removeDownload(UUID.randomUUID())

        verify(downloadRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(downloadRepository)

        StepVerifier.create(returned)
            .expectError(DownloadLinkNotFound::class.java)
            .verify()
    }

    @Test
    fun removeDownload() {
        `when`(downloadRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(downloadLink))
        `when`(downloadRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = downloadRequestService.removeDownload(UUID.randomUUID())

        verify(downloadRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(downloadRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(downloadLink.id)
                assertThat(it.pluginId).isEqualTo(downloadLink.pluginId)
                assertThat(it.version).isEqualTo(downloadLink.version)
                assertTrue(it.available)
            }
            .verifyComplete()
    }
}