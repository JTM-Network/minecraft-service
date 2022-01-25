package com.jtm.minecraft.data.security

import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.manager.AuthenticationManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.Authentication
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
class SecurityContextRepositoryTest {

    private val authenticationManager: AuthenticationManager = mock()
    private val tokenProvider: AccountTokenProvider = mock()
    private val securityContextRepository = SecurityContextRepository(authenticationManager, tokenProvider)
    private val exchange: ServerWebExchange = mock()

    @Before
    fun setup() {
        val request: ServerHttpRequest = mock()
        val headers: HttpHeaders = mock()

        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst("PLUGIN_AUTHORIZATION")).thenReturn("test")
        `when`(exchange.request).thenReturn(request)
    }

    @Test
    fun loadTest() {
        val auth: Authentication = mock()

        `when`(authenticationManager.authenticate(anyOrNull())).thenReturn(Mono.just(auth))
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getPluginId(anyString())).thenReturn(UUID.randomUUID())

        securityContextRepository.load(exchange)

        verify(exchange, times(1)).request
        verifyNoMoreInteractions(exchange)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getPluginId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(authenticationManager, times(1)).authenticate(anyOrNull())
        verifyNoMoreInteractions(authenticationManager)
    }
}