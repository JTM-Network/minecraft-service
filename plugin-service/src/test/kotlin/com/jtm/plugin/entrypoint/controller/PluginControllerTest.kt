package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.core.domain.model.PageSupport
import com.jtm.plugin.data.service.PluginService
import com.jtm.plugin.entrypoint.controller.PluginController
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
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
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(PluginController::class)
@AutoConfigureWebTestClient
class PluginControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var pluginService: PluginService

    private val plugin = Plugin(name = "Test", basic_description = "Basic", description = "Desc")
    private val pluginTwo = Plugin(name = "Test #2", basic_description = "Basic Desc #2", description = "Desc #2")
    private val dto = PluginDto(name = "Test #1", basic_description = "Basic description", description = "Description", version = "0.1", active = true, price = 10.50)

    @Test
    fun postPlugin() {
        `when`(pluginService.insertPlugin(anyOrNull())).thenReturn(Mono.just(plugin))

        testClient.post()
            .uri("/")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("Test")
            .jsonPath("$.basic_description").isEqualTo("Basic")
            .jsonPath("$.description").isEqualTo("Desc")

        verify(pluginService, times(1)).insertPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun getPlugin() {
        `when`(pluginService.getPlugin(anyOrNull(), anyOrNull())).thenReturn(Mono.just(plugin))

        testClient.get()
            .uri("/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("Test")
            .jsonPath("$.basic_description").isEqualTo("Basic")
            .jsonPath("$.description").isEqualTo("Desc")

        verify(pluginService, times(1)).getPlugin(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun getPlugins() {
        `when`(pluginService.getPlugins(anyOrNull())).thenReturn(Flux.just(plugin, pluginTwo))

        testClient.get()
            .uri("/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].name").isEqualTo("Test")
            .jsonPath("$[0].basic_description").isEqualTo("Basic")
            .jsonPath("$[0].description").isEqualTo("Desc")
            .jsonPath("$[1].name").isEqualTo("Test #2")
            .jsonPath("$[1].basic_description").isEqualTo("Basic Desc #2")
            .jsonPath("$[1].description").isEqualTo("Desc #2")

        verify(pluginService, times(1)).getPlugins(anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun getPluginsPaginated() {
        `when`(pluginService.getPluginsPaginated(anyOrNull(), anyOrNull())).thenReturn(Mono.just(PageSupport(mutableListOf(plugin, pluginTwo), 1, 5, 2)))

        testClient.get()
            .uri("/paginated")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.content[0].name").isEqualTo("Test")
            .jsonPath("$.content[0].basic_description").isEqualTo("Basic")
            .jsonPath("$.content[0].description").isEqualTo("Desc")
            .jsonPath("$.content[1].name").isEqualTo("Test #2")
            .jsonPath("$.content[1].basic_description").isEqualTo("Basic Desc #2")
            .jsonPath("$.content[1].description").isEqualTo("Desc #2")
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.pageSize").isEqualTo(5)
            .jsonPath("$.totalElements").isEqualTo(2)

        verify(pluginService, times(1)).getPluginsPaginated(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun getPluginsSearch() {
        `when`(pluginService.getPluginsBySearch(anyString(), anyOrNull(), anyOrNull())).thenReturn(Mono.just(PageSupport(mutableListOf(pluginTwo), 1, 5, 1)))

        testClient.get()
            .uri("/search/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.content[0].name").isEqualTo("Test #2")
            .jsonPath("$.content[0].basic_description").isEqualTo("Basic Desc #2")
            .jsonPath("$.content[0].description").isEqualTo("Desc #2")
            .jsonPath("$.pageNumber").isEqualTo(1)
            .jsonPath("$.pageSize").isEqualTo(5)
            .jsonPath("$.totalElements").isEqualTo(1)

        verify(pluginService, times(1)).getPluginsBySearch(anyString(), anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test
    fun deletePlugin() {
        `when`(pluginService.deletePlugin(anyOrNull())).thenReturn(Mono.just(plugin))

        testClient.delete()
            .uri("/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("Test")
            .jsonPath("$.basic_description").isEqualTo("Basic")
            .jsonPath("$.description").isEqualTo("Desc")

        verify(pluginService, times(1)).deletePlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }
}