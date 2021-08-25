package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.entity.DownloadLink
import com.jtm.minecraft.core.domain.entity.plugin.PluginVersion
import com.jtm.minecraft.core.domain.exceptions.InvalidUserDownload
import com.jtm.minecraft.core.domain.exceptions.RemoteAddressInvalid
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionFileNotFound
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.repository.DownloadLinkRepository
import com.jtm.minecraft.core.usecase.repository.plugin.PluginVersionRepository
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
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.File
import java.net.InetSocketAddress
import java.util.*

@RunWith(SpringRunner::class)
class DownloadServiceTest {

    private val linkRepository: DownloadLinkRepository = mock()
    private val versionRepository: PluginVersionRepository = mock()
    private val fileHandler: FileHandler = mock()
    private val downloadService = DownloadService(linkRepository, versionRepository, fileHandler)

    private val link = DownloadLink(pluginId = UUID.randomUUID(), version = "0.1", ipAddress = "127.0.0.1")
    private val version = PluginVersion(pluginId = UUID.randomUUID(), pluginName = "name", version = "0.1", changelog = "Change log")
    private val remoteAddress = InetSocketAddress("localhost", 3306)

    private val request: ServerHttpRequest = mock()
    private val response: ServerHttpResponse = mock()
    private val headers: HttpHeaders = mock()
    private val file: File = mock()

    @Before
    fun setup() {

        `when`(response.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("Bearer test")
        `when`(file.name).thenReturn("test.jar")
    }

    @Test
    fun downloadVersion_thenRemoteAddressInvalid() {
        `when`(request.remoteAddress).thenReturn(null)

        val returned = downloadService.downloadVersion(request, response, UUID.randomUUID())

        verify(request, times(1)).remoteAddress
        verifyNoMoreInteractions(request)

        StepVerifier.create(returned)
            .expectError(RemoteAddressInvalid::class.java)
            .verify()
    }

    @Test
    fun downloadVersion_thenInvalidUserDownload() {
        `when`(request.remoteAddress).thenReturn(InetSocketAddress("137.0.0.1", 3234))
        `when`(linkRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(link))

        val returned = downloadService.downloadVersion(request, response, UUID.randomUUID())

        verify(request, times(1)).remoteAddress
        verifyNoMoreInteractions(request)

        verify(linkRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(linkRepository)

        StepVerifier.create(returned)
            .expectError(InvalidUserDownload::class.java)
            .verify()
    }

    @Test
    fun downloadVersion_thenVersionFileNotFound() {
        `when`(request.remoteAddress).thenReturn(remoteAddress)
        `when`(linkRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(link))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))
        `when`(fileHandler.fetch(anyString())).thenReturn(Mono.just(file))
        `when`(file.exists()).thenReturn(false)

        val returned = downloadService.downloadVersion(request, response, UUID.randomUUID())

        verify(request, times(1)).remoteAddress
        verifyNoMoreInteractions(request)

        verify(linkRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(linkRepository)

        StepVerifier.create(returned)
            .expectError(VersionFileNotFound::class.java)
            .verify()
    }

    @Test
    fun downloadVersionTest() {
        `when`(request.remoteAddress).thenReturn(remoteAddress)
        `when`(linkRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(link))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))
        `when`(fileHandler.fetch(anyString())).thenReturn(Mono.just(file))
        `when`(file.exists()).thenReturn(true)

        val returned = downloadService.downloadVersion(request, response, UUID.randomUUID())

        verify(request, times(1)).remoteAddress
        verifyNoMoreInteractions(request)

        verify(linkRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(linkRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.filename).isEqualTo("test.jar") }
            .verifyComplete()
    }
}