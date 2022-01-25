package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.model.PluginIntent
import com.jtm.minecraft.data.service.plugin.IntentService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(IntentController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class IntentControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var intentService: IntentService

    @Test
    fun postIntentTest() {
        `when`(intentService.createIntent(anyOrNull(), anyOrNull())).thenReturn(Mono.just("test"))

        testClient.post()
            .uri("/intent/plugin")
            .bodyValue(PluginIntent(20.0, "USD", listOf(UUID.randomUUID())))
            .exchange()
            .expectStatus().isOk

        verify(intentService, times(1)).createIntent(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(intentService)
    }
}