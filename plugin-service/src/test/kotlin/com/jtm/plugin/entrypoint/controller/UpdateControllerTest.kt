package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.data.service.UpdateService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
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
class UpdateControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var updateService: UpdateService

    private val plugin = Plugin(name = "Test", basic_description = "Basic", description = "Desc")
    private val dto = PluginDto(id = UUID.randomUUID(), name = "Test #1", basic_description = "Basic description", description = "Description", version = "0.1", active = true, price = 10.50)

    @Test
    fun putName() {
        `when`(updateService.updateName(anyOrNull())).thenReturn(Mono.just(plugin))

        testClient.put()
            .uri("/update/name")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("Test")
            .jsonPath("$.basic_description").isEqualTo("Basic")
            .jsonPath("$.description").isEqualTo("Desc")

        verify(updateService, Mockito.times(1)).updateName(anyOrNull())
        verifyNoMoreInteractions(updateService)
    }

    @Test
    fun putBasicDesc() {
        `when`(updateService.updateBasicDesc(anyOrNull())).thenReturn(Mono.just(plugin))

        testClient.put()
            .uri("/update/basic-desc")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("Test")
            .jsonPath("$.basic_description").isEqualTo("Basic")
            .jsonPath("$.description").isEqualTo("Desc")

        verify(updateService, Mockito.times(1)).updateBasicDesc(anyOrNull())
        verifyNoMoreInteractions(updateService)
    }

    @Test
    fun putDesc() {
        `when`(updateService.updateDesc(anyOrNull())).thenReturn(Mono.just(plugin))

        testClient.put()
            .uri("/update/desc")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("Test")
            .jsonPath("$.basic_description").isEqualTo("Basic")
            .jsonPath("$.description").isEqualTo("Desc")

        verify(updateService, Mockito.times(1)).updateDesc(anyOrNull())
        verifyNoMoreInteractions(updateService)
    }

    @Test
    fun putVersion() {
        `when`(updateService.updateVersion(dto)).thenReturn(Mono.just(plugin))

        testClient.put()
            .uri("/update/version")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("Test")
            .jsonPath("$.basic_description").isEqualTo("Basic")
            .jsonPath("$.description").isEqualTo("Desc")

        verify(updateService, Mockito.times(1)).updateVersion(anyOrNull())
        verifyNoMoreInteractions(updateService)
    }

    @Test
    fun putActive() {
        `when`(updateService.updateActive(anyOrNull())).thenReturn(Mono.just(plugin))

        testClient.put()
            .uri("/update/active")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("Test")
            .jsonPath("$.basic_description").isEqualTo("Basic")
            .jsonPath("$.description").isEqualTo("Desc")

        verify(updateService, Mockito.times(1)).updateActive(anyOrNull())
        verifyNoMoreInteractions(updateService)
    }

    @Test
    fun putPrice() {
        `when`(updateService.updatePrice(anyOrNull())).thenReturn(Mono.just(plugin))

        testClient.put()
            .uri("/update/price")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("Test")
            .jsonPath("$.basic_description").isEqualTo("Basic")
            .jsonPath("$.description").isEqualTo("Desc")

        verify(updateService, Mockito.times(1)).updatePrice(anyOrNull())
        verifyNoMoreInteractions(updateService)
    }
}