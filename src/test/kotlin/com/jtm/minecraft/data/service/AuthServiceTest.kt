package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.entity.BlacklistToken
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginNotFound
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginUnauthorized
import com.jtm.minecraft.core.domain.exceptions.token.BlacklistTokenFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class AuthServiceTest {

    private val tokenProvider: AccountTokenProvider = mock()
    private val pluginService: PluginService = mock()
    private val profileService: ProfileService = mock()
    private val tokenService: BlacklistTokenService = mock()
    private val authService = AuthService(tokenProvider, pluginService, tokenService)

    private val request: ServerHttpRequest = mock()
    private val plugin: Plugin = mock()
    private val created = BlacklistToken("token")
    private val profile: Profile = mock()

    @Before
    fun setup() {
        val headers: HttpHeaders = mock()

        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("Bearer token")
    }

    @Test
    fun authenticate_thenAccountIdInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = authService.authenticate(request, profileService,"test")

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun authenticate_thenAccountEmailInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(tokenProvider.getAccountEmail(anyString())).thenReturn(null)

        val returned = authService.authenticate(request, profileService, "test")

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verify(tokenProvider, times(1)).getAccountEmail(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun authenticate_thenPluginNotFound() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(tokenProvider.getAccountEmail(anyString())).thenReturn("test@gmail.com")
        `when`(pluginService.getPluginByName(anyString())).thenReturn(Mono.empty())

        val returned = authService.authenticate(request, profileService, "test")

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verify(tokenProvider, times(1)).getAccountEmail(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPluginByName(anyString())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun authenticate_thenPluginUnauthorized() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(tokenProvider.getAccountEmail(anyString())).thenReturn("test@gmail.com")
        `when`(tokenProvider.createPluginToken(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn("token")
        `when`(pluginService.getPluginByName(anyString())).thenReturn(Mono.just(plugin))
        `when`(plugin.id).thenReturn(UUID.randomUUID())
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))
        `when`(profile.isAuthenticated(anyOrNull())).thenReturn(false)

        val returned = authService.authenticate(request, profileService, "name")

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verify(tokenProvider, times(1)).getAccountEmail(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPluginByName(anyString())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .expectError(PluginUnauthorized::class.java)
            .verify()
    }

    @Test
    fun authenticateTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(tokenProvider.getAccountEmail(anyString())).thenReturn("test@gmail.com")
        `when`(tokenProvider.createPluginToken(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn("token")
        `when`(pluginService.getPluginByName(anyString())).thenReturn(Mono.just(plugin))
        `when`(plugin.id).thenReturn(UUID.randomUUID())
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))
        `when`(profile.isAuthenticated(anyOrNull())).thenReturn(true)

        val returned = authService.authenticate(request, profileService, "name")

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verify(tokenProvider, times(1)).getAccountEmail(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPluginByName(anyString())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.token).isEqualTo("token")
            }
            .verifyComplete()
    }

    @Test
    fun blacklistToken_thenTokenFound() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenService.isBlacklisted(anyString())).thenReturn(Mono.just(true))

        val returned = authService.blacklistToken(request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(tokenService, times(1)).isBlacklisted(anyString())
        verifyNoMoreInteractions(tokenService)

        StepVerifier.create(returned)
            .expectError(BlacklistTokenFound::class.java)
            .verify()
    }

    @Test
    fun blacklistTokenTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenService.isBlacklisted(anyString())).thenReturn(Mono.just(false))
        `when`(tokenService.insertToken(anyString())).thenReturn(Mono.just(created))

        val returned = authService.blacklistToken(request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(tokenService, times(1)).isBlacklisted(anyString())
        verifyNoMoreInteractions(tokenService)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.token).isEqualTo("token")
                assertThat(it.timestamp).isLessThan(System.currentTimeMillis())
            }
            .verifyComplete()
    }
}