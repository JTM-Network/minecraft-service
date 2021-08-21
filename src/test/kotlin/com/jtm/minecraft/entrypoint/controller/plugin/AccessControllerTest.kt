package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.data.service.plugin.AccessService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
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
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(AccessController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class AccessControllerTest {

    @Autowired lateinit var testClient: WebTestClient
    @MockBean lateinit var accessService: AccessService
    private val profile = Profile(email = "test@gmail.com")

    @Test
    fun addAccessTest() {
        `when`(accessService.addAccess(anyOrNull(), anyOrNull())).thenReturn(Mono.empty())

        testClient.get()
            .uri("/access/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk

        verify(accessService, times(1)).addAccess(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(accessService)
    }

    @Test
    fun hasAccessTest() {
        `when`(accessService.hasAccess(anyString(), anyOrNull())).thenReturn(Mono.empty())

        testClient.get()
            .uri("/access/check/test")
            .exchange()
            .expectStatus().isOk

        verify(accessService, times(1)).hasAccess(anyString(), anyOrNull())
        verifyNoMoreInteractions(accessService)
    }

    @Test
    fun removeAccessTest() {
        `when`(accessService.removeAccess(anyOrNull(), anyOrNull())).thenReturn(Mono.just(profile))

        testClient.delete()
            .uri("/access?plugin=${UUID.randomUUID()}&account=${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.email").isEqualTo("test@gmail.com")

        verify(accessService, times(1)).removeAccess(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(accessService)
    }
}