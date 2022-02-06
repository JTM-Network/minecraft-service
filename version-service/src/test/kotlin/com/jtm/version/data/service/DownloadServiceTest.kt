package com.jtm.version.data.service

import com.jtm.version.core.domain.entity.DownloadLink
import com.jtm.version.core.domain.entity.Version
import com.jtm.version.core.domain.exceptions.download.DownloadLinkNotFound
import com.jtm.version.core.domain.exceptions.version.VersionNotFound
import com.jtm.version.core.usecase.file.FileSystemHandler
import com.jtm.version.core.usecase.repository.DownloadRepository
import com.jtm.version.core.usecase.repository.VersionRepository
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
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.File
import java.util.*

@RunWith(SpringRunner::class)
class DownloadServiceTest {

    private val downloadRepository: DownloadRepository = mock()
    private val versionRepository: VersionRepository = mock()
    private val systemHandler: FileSystemHandler = mock()
    private val downloadService = DownloadService(downloadRepository, versionRepository, systemHandler)

    private val link = DownloadLink(pluginId = UUID.randomUUID(), version = "1.0", clientId = "id")
    private val version = Version(pluginId = link.pluginId, pluginName = "test", version = link.version, changelog = "Changelog")
    private val file: File = mock()

    private val response: ServerHttpResponse = mock()
    private val headers: HttpHeaders = mock()

    @Before
    fun setup() {
        `when`(response.headers).thenReturn(headers)
        `when`(file.name).thenReturn("test.jar")
    }

    @Test
    fun getDownload_thenLinkNotFound() {
        `when`(downloadRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = downloadService.getDownload(response, UUID.randomUUID())

        verify(downloadRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(downloadRepository)

        StepVerifier.create(returned)
            .expectError(DownloadLinkNotFound::class.java)
            .verify()
    }

    @Test
    fun getDownload_thenVersionNotFound() {
        `when`(downloadRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(link))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.empty())

        val returned = downloadService.getDownload(response, UUID.randomUUID())

        verify(downloadRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(downloadRepository)

        StepVerifier.create(returned)
            .expectError(VersionNotFound::class.java)
            .verify()
    }

    @Test
    fun getDownload() {
        `when`(downloadRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(link))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))
        `when`(systemHandler.fetch(anyString())).thenReturn(Mono.just(file))

        val returned = downloadService.getDownload(response, UUID.randomUUID())

        verify(downloadRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(downloadRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.filename).isEqualTo("test.jar") }
            .verifyComplete()
    }
}