package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.dto.PluginDto
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.domain.model.PageSupport
import com.jtm.minecraft.data.service.PluginService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(PluginController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class PluginControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var pluginService: PluginService
    private val created = Plugin(name = "test", description = "test")
    private val createdTwo = Plugin(name = "test #2", description = "desc #2")
    private val dto = PluginDto("test", "test", 20.0, false)

    @Test
    fun postPluginTest() {
        `when`(pluginService.insertPlugin(anyOrNull())).thenReturn(Mono.just(created))

        testClient.post()
            .uri("/plugin")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.description").isEqualTo("test")

        verify(pluginService, times(1)).insertPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun putPluginNameTest() {
        `when`(pluginService.updateName(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
            .uri("/plugin/${UUID.randomUUID()}/name")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.description").isEqualTo("test")

        verify(pluginService, times(1)).updateName(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun putPluginDescTest() {
        `when`(pluginService.updateDesc(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
            .uri("/plugin/${UUID.randomUUID()}/desc")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.description").isEqualTo("test")

        verify(pluginService, times(1)).updateDesc(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun putPluginPriceTest() {
        `when`(pluginService.updatePrice(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
            .uri("/plugin/${UUID.randomUUID()}/price")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.description").isEqualTo("test")

        verify(pluginService, times(1)).updatePrice(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun putPluginActiveTest() {
        `when`(pluginService.updateActive(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
            .uri("/plugin/${UUID.randomUUID()}/active")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.description").isEqualTo("test")

        verify(pluginService, times(1)).updateActive(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun getPluginTest() {
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/plugin/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.description").isEqualTo("test")

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun getPluginByNameTest() {
        `when`(pluginService.getPluginByName(anyString())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/plugin/name/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.description").isEqualTo("test")

        verify(pluginService, times(1)).getPluginByName(anyString())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun getPluginsTest() {
        `when`(pluginService.getPlugins()).thenReturn(Flux.just(created, Plugin(name = "test #1", description = "desc #1")))

        testClient.get()
            .uri("/plugin/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].name").isEqualTo("test")
            .jsonPath("$[0].description").isEqualTo("test")
            .jsonPath("$[1].name").isEqualTo("test #1")
            .jsonPath("$[1].description").isEqualTo("desc #1")

        verify(pluginService, times(1)).getPlugins()
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun getPluginsListTest() {
        `when`(pluginService.getPluginsSortable(anyOrNull())).thenReturn(Mono.just(PageSupport(mutableListOf(created, createdTwo), 1, 5, 2)))

        testClient.get()
            .uri("/plugin/list")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.content[0].name").isEqualTo("test")
            .jsonPath("$.content[0].description").isEqualTo("test")
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.pageSize").isEqualTo(5)
            .jsonPath("$.totalElements").isEqualTo(2)

        verify(pluginService, times(1)).getPluginsSortable(anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun getPluginsSearchTest() {
        `when`(pluginService.getPluginsBySearch(anyString(), anyOrNull())).thenReturn(Mono.just(PageSupport(mutableListOf(created, createdTwo), 1, 5, 2)))

        testClient.get()
            .uri("/plugin/search/plug")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.content[0].name").isEqualTo("test")
            .jsonPath("$.content[0].description").isEqualTo("test")
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.pageSize").isEqualTo(5)
            .jsonPath("$.totalElements").isEqualTo(2)

        verify(pluginService, times(1)).getPluginsBySearch(anyString(), anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun deletePluginTest() {
        `when`(pluginService.removePlugin(anyOrNull())).thenReturn(Mono.just(created))

        testClient.delete()
            .uri("/plugin/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.description").isEqualTo("test")

        verify(pluginService, times(1)).removePlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }
}