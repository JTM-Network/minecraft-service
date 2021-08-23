package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.entity.DownloadLink
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.domain.entity.plugin.PluginVersion
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionFileNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.repository.DownloadLinkRepository
import com.jtm.minecraft.core.usecase.repository.plugin.PluginVersionRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.PluginService
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
import java.util.*

@RunWith(SpringRunner::class)
class DownloadServiceTest {

    private val linkRepository: DownloadLinkRepository = mock()
    private val versionRepository: PluginVersionRepository = mock()
    private val tokenProvider: AccountTokenProvider = mock()
    private val fileHandler: FileHandler = mock()
    private val downloadService = DownloadService(linkRepository, versionRepository, tokenProvider, fileHandler)
    private val request: ServerHttpRequest = mock()
    private val response: ServerHttpResponse = mock()
    private val headers: HttpHeaders = mock()
    private val link = DownloadLink(pluginId = UUID.randomUUID(), version = "0.1", accountId = UUID.randomUUID())
    private val version = PluginVersion(pluginId = UUID.randomUUID(), pluginName = "name", version = "0.1", changelog = "Change log")
    private val file: File = mock()

    @Before
    fun setup() {
        `when`(request.headers).thenReturn(headers)
        `when`(response.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("Bearer test")
        `when`(file.name).thenReturn("test.jar")

        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
    }

    @Test
    fun downloadVersion_thenAccountIdInvalid() {
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = downloadService.downloadVersion(request, response, UUID.randomUUID())

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun downloadVersion_thenVersionFileNotFound() {
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(linkRepository.findByIdAndAccountId(anyOrNull(), anyOrNull())).thenReturn(Mono.just(link))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))
        `when`(fileHandler.fetch(anyString())).thenReturn(Mono.just(file))
        `when`(file.exists()).thenReturn(false)

        val returned = downloadService.downloadVersion(request, response, UUID.randomUUID())

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(linkRepository, times(1)).findByIdAndAccountId(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(linkRepository)

        StepVerifier.create(returned)
            .expectError(VersionFileNotFound::class.java)
            .verify()
    }

    @Test
    fun downloadVersionTest() {
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(linkRepository.findByIdAndAccountId(anyOrNull(), anyOrNull())).thenReturn(Mono.just(link))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))
        `when`(fileHandler.fetch(anyString())).thenReturn(Mono.just(file))
        `when`(file.exists()).thenReturn(true)

        val returned = downloadService.downloadVersion(request, response, UUID.randomUUID())

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(linkRepository, times(1)).findByIdAndAccountId(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(linkRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.filename).isEqualTo("test.jar") }
            .verifyComplete()
    }
}