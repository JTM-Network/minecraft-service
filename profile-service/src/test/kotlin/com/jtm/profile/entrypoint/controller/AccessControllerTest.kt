package com.jtm.profile.entrypoint.controller

import com.jtm.profile.core.domain.dto.AccessDto
import com.jtm.profile.core.domain.exceptions.ProfileNotFound
import com.jtm.profile.data.service.AccessService
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
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(AccessController::class)
@AutoConfigureWebTestClient
class AccessControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var accessService: AccessService

    private val dto = AccessDto(UUID.randomUUID().toString(), listOf(UUID.randomUUID()))

    @Test
    fun addAccess_thenNotFound() {
        `when`(accessService.addAccess(anyOrNull())).thenReturn(Mono.error { ProfileNotFound() })

        testClient.post()
            .uri("/access")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Profile not found.")

        verify(accessService, times(1)).addAccess(anyOrNull())
        verifyNoMoreInteractions(accessService)
    }

    @Test
    fun addAccess() {
        `when`(accessService.addAccess(anyOrNull())).thenReturn(Mono.empty())

        testClient.post()
            .uri("/access")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk

        verify(accessService, times(1)).addAccess(anyOrNull())
        verifyNoMoreInteractions(accessService)
    }
}