package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.data.service.HookService
import com.stripe.model.Event
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
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

@RunWith(SpringRunner::class)
@WebFluxTest(HookController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class HookControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var hookService: HookService
    private val event: Event = mock()

    @Test
    fun confirmAccessTest() {
        `when`(hookService.addAccess(anyOrNull())).thenReturn(Mono.empty())

        testClient.post()
            .uri("/hook/plugin")
            .bodyValue(event)
            .exchange()
            .expectStatus().isOk

        verify(hookService, times(1)).addAccess(anyOrNull())
        verifyNoMoreInteractions(hookService)
    }
}