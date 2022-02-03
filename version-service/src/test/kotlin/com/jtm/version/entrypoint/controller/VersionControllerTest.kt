package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.entity.Version
import com.jtm.version.data.service.VersionService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(VersionController::class)
@AutoConfigureWebTestClient
class VersionControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var versionService: VersionService

    private val version = Version(pluginId = UUID.randomUUID(), version = "1.0")

    @Test
    fun getVersion() {
        `when`(versionService.getVersion(anyOrNull())).thenReturn(Mono.just(version))

        testClient.get()
            .uri("/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(version.id.toString())
            .jsonPath("$.pluginId").isEqualTo(version.pluginId.toString())

        verify(versionService, times(1)).getVersion(anyOrNull())
        verifyNoMoreInteractions(versionService)
    }

    @Test
    fun getVersionByPlugin() {
        `when`(versionService.getVersionsByPlugin(anyOrNull())).thenReturn(Flux.just(version))

        testClient.get()
            .uri("/plugin/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(version.id.toString())
            .jsonPath("$[0].pluginId").isEqualTo(version.pluginId.toString())

        verify(versionService, times(1)).getVersionsByPlugin(anyOrNull())
        verifyNoMoreInteractions(versionService)
    }

    @Test
    fun getVersions() {
        `when`(versionService.getVersions()).thenReturn(Flux.just(version))

        testClient.get()
            .uri("/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(version.id.toString())
            .jsonPath("$[0].pluginId").isEqualTo(version.pluginId.toString())

        verify(versionService, times(1)).getVersions()
        verifyNoMoreInteractions(versionService)
    }

    @Test
    fun deleteVersion() {
        `when`(versionService.deleteVersion(anyOrNull())).thenReturn(Mono.just(version))

        testClient.delete()
            .uri("/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(version.id.toString())
            .jsonPath("$.pluginId").isEqualTo(version.pluginId.toString())

        verify(versionService, times(1)).deleteVersion(anyOrNull())
        verifyNoMoreInteractions(versionService)
    }
}