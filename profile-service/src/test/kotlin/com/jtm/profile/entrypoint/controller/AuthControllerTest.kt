package com.jtm.profile.entrypoint.controller

import com.jtm.profile.core.domain.dto.AuthDto
import com.jtm.profile.data.service.AuthService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(AuthController::class)
@AutoConfigureWebTestClient
class AuthControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var authService: AuthService

    @Test
    fun isAuthorized() {
        `when`(authService.isAuthorized(anyString(), anyOrNull())).thenReturn(Mono.empty())

        testClient.post()
            .uri("/authorize/check")
            .bodyValue(AuthDto("id", UUID.randomUUID()))
            .exchange()
            .expectStatus().isOk

        verify(authService, times(1)).isAuthorized(anyString(), anyOrNull())
        verifyNoMoreInteractions(authService)
    }
}