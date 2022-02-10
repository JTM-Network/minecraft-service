package com.jtm.profile.entrypoint.controller

import com.jtm.profile.core.domain.entity.Token
import com.jtm.profile.data.service.TokenService
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
@WebFluxTest(TokenController::class)
@AutoConfigureWebTestClient
class TokenControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var tokenService: TokenService

    private val token = Token(token = "token", clientId = "clientId")
    private val tokenTwo = Token(token = "token2", clientId = "clientId2")

    @Test
    fun generateToken() {
        `when`(tokenService.generateToken(anyOrNull())).thenReturn(Mono.just(token))

        testClient.get()
            .uri("/token/generate")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isEqualTo("token")
            .jsonPath("$.clientId").isEqualTo("clientId")

        verify(tokenService, times(1)).generateToken(anyOrNull())
        verifyNoMoreInteractions(tokenService)
    }

    @Test
    fun getTokenById() {
        `when`(tokenService.getTokenById(anyOrNull())).thenReturn(Mono.just(token))

        testClient.get()
            .uri("/token/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isEqualTo("token")
            .jsonPath("$.clientId").isEqualTo("clientId")

        verify(tokenService, times(1)).getTokenById(anyOrNull())
        verifyNoMoreInteractions(tokenService)
    }

    @Test
    fun getToken() {
        `when`(tokenService.getToken(anyString())).thenReturn(Mono.just(token))

        testClient.get()
            .uri("/token?value=test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isEqualTo("token")
            .jsonPath("$.clientId").isEqualTo("clientId")

        verify(tokenService, times(1)).getToken(anyString())
        verifyNoMoreInteractions(tokenService)
    }

    @Test
    fun getTokens() {
        `when`(tokenService.getTokens()).thenReturn(Flux.just(token, tokenTwo))

        testClient.get()
            .uri("/token/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].token").isEqualTo("token")
            .jsonPath("$[0].clientId").isEqualTo("clientId")
            .jsonPath("$[1].token").isEqualTo("token2")
            .jsonPath("$[1].clientId").isEqualTo("clientId2")

        verify(tokenService, times(1)).getTokens()
        verifyNoMoreInteractions(tokenService)
    }

    @Test
    fun getTokensByAccount() {
        `when`(tokenService.getTokensByAccount(anyOrNull())).thenReturn(Flux.just(token, tokenTwo))

        testClient.get()
            .uri("/token/account")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].token").isEqualTo("token")
            .jsonPath("$[0].clientId").isEqualTo("clientId")
            .jsonPath("$[1].token").isEqualTo("token2")
            .jsonPath("$[1].clientId").isEqualTo("clientId2")

        verify(tokenService, times(1)).getTokensByAccount(anyOrNull())
        verifyNoMoreInteractions(tokenService)
    }

    @Test
    fun getTokensByAccountId() {
        `when`(tokenService.getTokensByAccountId(anyString())).thenReturn(Flux.just(token, tokenTwo))

        testClient.get()
            .uri("/token/account/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].token").isEqualTo("token")
            .jsonPath("$[0].clientId").isEqualTo("clientId")
            .jsonPath("$[1].token").isEqualTo("token2")
            .jsonPath("$[1].clientId").isEqualTo("clientId2")

        verify(tokenService, times(1)).getTokensByAccountId(anyString())
        verifyNoMoreInteractions(tokenService)
    }

    @Test
    fun deleteToken() {
        `when`(tokenService.removeToken(anyOrNull())).thenReturn(Mono.just(token))

        testClient.delete()
            .uri("/token/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.token").isEqualTo("token")
            .jsonPath("$.clientId").isEqualTo("clientId")

        verify(tokenService, times(1)).removeToken(anyOrNull())
        verifyNoMoreInteractions(tokenService)
    }
}