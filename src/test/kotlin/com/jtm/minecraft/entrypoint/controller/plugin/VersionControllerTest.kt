package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.dto.PluginVersionDto
import com.jtm.minecraft.core.domain.entity.plugin.PluginVersion
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.plugin.AccessService
import com.jtm.minecraft.data.service.plugin.VersionService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(VersionController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class VersionControllerTest {

    @Autowired lateinit var testClient: WebTestClient
    @MockBean lateinit var versionService: VersionService
    @MockBean lateinit var fileHandler: FileHandler
    @MockBean lateinit var accessService: AccessService
    @MockBean lateinit var tokenProvider: AccountTokenProvider

    private val dto = PluginVersionDto(pluginId = UUID.randomUUID(), file = null, version = "0.1", changelog = "Change log.")
    private val created = PluginVersion(pluginId = UUID.randomUUID(), pluginName = "test", version = "0.1", changelog = "Changelog")

    @Test
    fun putVersionTest() {
        `when`(versionService.updateVersion(anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
            .uri("/version")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$.pluginName").isEqualTo(created.pluginName)
            .jsonPath("$.version").isEqualTo(created.version)

        verify(versionService, times(1)).updateVersion(anyOrNull())
        verifyNoMoreInteractions(versionService)
    }

    @Test
    fun getDownloadRequestTest() {
        `when`(versionService.downloadVersionRequest(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(Mono.just("test"))

        testClient.get()
            .uri("/version/download/request?name=test&version=0.1.1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isEqualTo("test")

        verify(versionService, times(1)).downloadVersionRequest(anyString(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(versionService)
    }

    @Test
    fun getVersionTest() {
        `when`(versionService.getVersion(anyOrNull())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/version/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$.pluginName").isEqualTo(created.pluginName)
            .jsonPath("$.version").isEqualTo(created.version)

        verify(versionService, times(1)).getVersion(anyOrNull())
        verifyNoMoreInteractions(versionService)
    }

    @Test
    fun getVersionsByPluginIdTest() {
        `when`(versionService.getVersionsByPluginId(anyOrNull())).thenReturn(Flux.just(created))

        testClient.get()
            .uri("/version/plugin/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$[0].pluginName").isEqualTo(created.pluginName)
            .jsonPath("$[0].version").isEqualTo(created.version)

        verify(versionService, times(1)).getVersionsByPluginId(anyOrNull())
        verifyNoMoreInteractions(versionService)
    }

    @Test
    fun getVersionByPluginNameTest() {
        `when`(versionService.getVersionsByPluginName(anyString())).thenReturn(Flux.just(created))

        testClient.get()
            .uri("/version/name/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$[0].pluginName").isEqualTo(created.pluginName)
            .jsonPath("$[0].version").isEqualTo(created.version)

        verify(versionService, times(1)).getVersionsByPluginName(anyString())
        verifyNoMoreInteractions(versionService)
    }

    @Test
    fun getVersionsTest() {
        `when`(versionService.getVersions()).thenReturn(Flux.just(created))

        testClient.get()
            .uri("/version/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$[0].pluginName").isEqualTo(created.pluginName)
            .jsonPath("$[0].version").isEqualTo(created.version)

        verify(versionService, times(1)).getVersions()
        verifyNoMoreInteractions(versionService)
    }

    @Test
    fun removeVersionTest() {
        `when`(versionService.removeVersion(anyOrNull())).thenReturn(Mono.just(created))

        testClient.delete()
            .uri("/version/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$.pluginName").isEqualTo(created.pluginName)
            .jsonPath("$.version").isEqualTo(created.version)

        verify(versionService, times(1)).removeVersion(anyOrNull())
        verifyNoMoreInteractions(versionService)
    }
}