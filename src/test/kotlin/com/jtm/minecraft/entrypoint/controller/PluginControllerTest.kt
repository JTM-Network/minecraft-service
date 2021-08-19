package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.dto.PluginDto
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.data.service.PluginService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
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

    @Test fun postPluginTest() {
        `when`(pluginService.insertPlugin(anyOrNull())).thenReturn(Mono.just(created))

        testClient.post()
            .uri("/plugin")
            .bodyValue(PluginDto("test", "test"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.description").isEqualTo("test")

        verify(pluginService, times(1)).insertPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test fun putPluginTest() {
        `when`(pluginService.updatePlugin(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
            .uri("/plugin/${UUID.randomUUID()}")
            .bodyValue(PluginDto("test", "test"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.description").isEqualTo("test")

        verify(pluginService, times(1)).updatePlugin(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(pluginService)
    }

    @Test fun getPluginTest() {
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

    @Test fun getPluginByNameTest() {
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

    @Test fun getPluginsTest() {
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

    @Test fun deletePluginTest() {
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