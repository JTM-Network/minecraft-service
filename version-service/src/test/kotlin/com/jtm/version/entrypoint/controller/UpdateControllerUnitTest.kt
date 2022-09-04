package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.dto.UpdateDto
import com.jtm.version.core.domain.dto.VersionDto
import com.jtm.version.core.domain.entity.Version
import com.jtm.version.data.service.UpdateService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.times
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(UpdateController::class)
@AutoConfigureWebTestClient
class UpdateControllerUnitTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var updateService: UpdateService

    private val dto = VersionDto(UUID.randomUUID(), "Test", version = "1.0", changelog = "Changelog")
    private val version = Version(dto)
    private val update = UpdateDto("1.1", "Changelog test")

    @Test
    fun putVersion() {
        `when`(updateService.updateVersion(anyOrNull(), anyOrNull())).thenReturn(Mono.just(version))

        testClient.put()
            .uri("/update/${UUID.randomUUID()}/version")
            .bodyValue(update)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(version.id.toString())
            .jsonPath("$.pluginName").isEqualTo("Test")
            .jsonPath("$.version").isEqualTo("1.0")

        verify(updateService, times(1)).updateVersion(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(updateService)
    }

    @Test
    fun putChangelog() {
        `when`(updateService.updateChangelog(anyOrNull(), anyOrNull())).thenReturn(Mono.just(version))

        testClient.put()
            .uri("/update/${UUID.randomUUID()}/changelog")
            .bodyValue(update)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(version.id.toString())
            .jsonPath("$.pluginName").isEqualTo("Test")
            .jsonPath("$.version").isEqualTo("1.0")

        verify(updateService, times(1)).updateChangelog(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(updateService)
    }
}