package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.entity.BlacklistToken
import com.jtm.minecraft.core.domain.model.AuthToken
import com.jtm.minecraft.data.service.AuthService
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
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@WebFluxTest(AuthController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class AuthControllerTest {

    @Autowired lateinit var testClient: WebTestClient
    @MockBean lateinit var authService: AuthService

    @Test
    fun authenticateTest() {
        `when`(authService.authenticate(anyOrNull(), anyString())).thenReturn(Mono.just(AuthToken("token", "dsn")))

        testClient.get()
            .uri("/auth/authenticate?plugin=name")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isEqualTo("token")
            .jsonPath("$.sentryDsn").isEqualTo("dsn")

        verify(authService, times(1)).authenticate(anyOrNull(), anyString())
        verifyNoMoreInteractions(authService)
    }

    @Test
    fun blacklistTokenTest() {
        `when`(authService.blacklistToken(anyOrNull())).thenReturn(Mono.just(BlacklistToken("token")))

        testClient.delete()
            .uri("/auth/blacklist")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isEqualTo("token")

        verify(authService, times(1)).blacklistToken(anyOrNull())
        verifyNoMoreInteractions(authService)
    }
}