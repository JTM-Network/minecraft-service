package com.jtm.minecraft.data.manager

import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginHeaderMissing
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginUnauthorized
import com.jtm.minecraft.core.domain.exceptions.token.BlacklistTokenFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.core.util.Logging
import com.jtm.minecraft.data.service.BlacklistTokenService
import com.jtm.minecraft.data.service.ProfileService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class AuthenticationManagerTest {

    private val tokenProvider: AccountTokenProvider = mock()
    private val tokenService: BlacklistTokenService = mock()
    private val profileService: ProfileService = mock()
    private val logging: Logging = mock()
    private val authenticationManager = AuthenticationManager(tokenProvider, tokenService, profileService, logging)

    private val authentication: Authentication = mock()
    private val profile: Profile = mock()

    @Test
    fun authentication_thenTokenInvalid() {
        `when`(authentication.principal).thenReturn(null)

        val returned = authenticationManager.authenticate(authentication)

        verify(authentication, times(1)).principal
        verifyNoMoreInteractions(authentication)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun authentication_thenPluginHeaderMissing() {
        `when`(authentication.principal).thenReturn("test")
        `when`(authentication.credentials).thenReturn(null)

        val returned = authenticationManager.authenticate(authentication)

        verify(authentication, times(1)).principal
        verify(authentication, times(1)).credentials
        verifyNoMoreInteractions(authentication)

        StepVerifier.create(returned)
            .expectError(PluginHeaderMissing::class.java)
            .verify()
    }

    @Test
    fun authentication_thenAccountIdInvalid() {
        `when`(authentication.principal).thenReturn("test")
        `when`(authentication.credentials).thenReturn(UUID.randomUUID().toString())
        `when`(tokenProvider.getPluginAccountId(anyString())).thenReturn(null)

        val returned = authenticationManager.authenticate(authentication)

        verify(authentication, times(1)).principal
        verify(authentication, times(1)).credentials
        verifyNoMoreInteractions(authentication)

        verify(tokenProvider, times(1)).getPluginAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun authentication_thenBlacklistTokenFound() {
        `when`(authentication.principal).thenReturn("test")
        `when`(authentication.credentials).thenReturn(UUID.randomUUID().toString())
        `when`(tokenProvider.getPluginAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(tokenService.isBlacklisted(anyString())).thenReturn(Mono.just(true))

        val returned = authenticationManager.authenticate(authentication)

        verify(authentication, times(1)).principal
        verify(authentication, times(1)).credentials
        verifyNoMoreInteractions(authentication)

        verify(tokenProvider, times(1)).getPluginAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(tokenService, times(1)).isBlacklisted(anyString())
        verifyNoMoreInteractions(tokenService)

        StepVerifier.create(returned)
            .expectError(BlacklistTokenFound::class.java)
            .verify()
    }

    @Test
    fun authentication_thenPluginUnauthorized() {
        `when`(authentication.principal).thenReturn("test")
        `when`(authentication.credentials).thenReturn(UUID.randomUUID().toString())
        `when`(tokenProvider.getPluginAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(tokenService.isBlacklisted(anyString())).thenReturn(Mono.just(false))
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))
        `when`(profile.isAuthenticated(anyOrNull())).thenReturn(false)

        val returned = authenticationManager.authenticate(authentication)

        verify(authentication, times(1)).principal
        verify(authentication, times(1)).credentials
        verifyNoMoreInteractions(authentication)

        verify(tokenProvider, times(1)).getPluginAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(tokenService, times(1)).isBlacklisted(anyString())
        verifyNoMoreInteractions(tokenService)

        StepVerifier.create(returned)
            .expectError(PluginUnauthorized::class.java)
            .verify()
    }

    @Test
    fun authenticationTest() {
        `when`(authentication.principal).thenReturn("token")
        `when`(authentication.credentials).thenReturn(UUID.randomUUID())
        `when`(tokenProvider.getPluginAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(tokenService.isBlacklisted(anyString())).thenReturn(Mono.just(false))
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))
        `when`(profile.isAuthenticated(anyOrNull())).thenReturn(true)

        val returned = authenticationManager.authenticate(authentication)

        verify(authentication, times(1)).principal
        verify(authentication, times(1)).credentials
        verifyNoMoreInteractions(authentication)

        verify(tokenProvider, times(1)).getPluginAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(tokenService, times(1)).isBlacklisted(anyString())
        verifyNoMoreInteractions(tokenService)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.principal).isEqualTo("token")
                assertThat(it.credentials).isInstanceOf(UUID::class.java)
            }
            .verifyComplete()
    }
}