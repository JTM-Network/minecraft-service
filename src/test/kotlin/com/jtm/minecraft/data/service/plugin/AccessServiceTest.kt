package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginIsPremium
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginUnauthorized
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileAlreadyHasAccess
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileNoAccess
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.PluginService
import com.jtm.minecraft.data.service.ProfileService
import org.assertj.core.api.Assertions.assertThat
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
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class AccessServiceTest {

    private val profileService: ProfileService = mock()
    private val pluginService: PluginService = mock()
    private val tokenProvider: AccountTokenProvider = mock()
    private val accessService = AccessService(profileService, pluginService, tokenProvider)

    private val profile = Profile(email = "test@gmail.com")
    private val plugin = Plugin(name = "test", description = "desc")
    private val request: ServerHttpRequest = mock()
    private val headers: HttpHeaders = mock()

    @Before
    fun setup() {
        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("Bearer test")
    }

    @Test
    fun addAccess_thenAccountIdInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = accessService.addAccess(UUID.randomUUID(), request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun addAccess_thenPluginIsPremium() {
        plugin.premium = true

        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))

        val returned = accessService.addAccess(UUID.randomUUID(), request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .expectError(PluginIsPremium::class.java)
            .verify()
    }

    @Test
    fun addAccess_thenProfileAlreadyHasAccess() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile.addAccess(plugin.id)))

        val returned = accessService.addAccess(plugin.id, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .expectError(ProfileAlreadyHasAccess::class.java)
            .verify()
    }

    @Test
    fun addAccessTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))
        `when`(profileService.updateProfile(anyOrNull())).thenReturn(Mono.just(profile))

        val returned = accessService.addAccess(plugin.id, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .verifyComplete()
    }

    @Test
    fun addPremiumAccess_thenProfileNotFound() {
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.error { ProfileNotFound() })

        val returned = accessService.addPremiumAccess(UUID.randomUUID(), arrayOf(UUID.randomUUID()))

        verify(profileService, times(1)).getProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun addPremiumAccessTest() {
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))
        `when`(profileService.updateProfile(anyOrNull())).thenReturn(Mono.just(profile.addAccess(arrayOf(plugin.id))))

        val returned = accessService.addPremiumAccess(UUID.randomUUID(), arrayOf(plugin.id))

        verify(profileService, times(1)).getProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.isAuthenticated(plugin.id)).isTrue }
            .verifyComplete()
    }

    @Test
    fun hasAccess_thenAccountIdInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = accessService.hasAccess("test", request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun hasAccess_thenPluginUnauthorized() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPluginByName(anyString())).thenReturn(Mono.just(plugin))
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))

        val returned = accessService.hasAccess("test", request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPluginByName(anyString())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .expectError(PluginUnauthorized::class.java)
            .verify()
    }

    @Test
    fun hasAccessTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPluginByName(anyString())).thenReturn(Mono.just(plugin))
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile.addAccess(plugin.id)))

        val returned = accessService.hasAccess("test", request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPluginByName(anyString())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .verifyComplete()
    }

    @Test
    fun removeAccess_thenProfileNoAccess() {
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))

        val returned = accessService.removeAccess(plugin.id, UUID.randomUUID())

        verify(profileService, times(1)).getProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)

        StepVerifier.create(returned)
            .expectError(ProfileNoAccess::class.java)
            .verify()
    }

    @Test
    fun removeAccessTest() {
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile.addAccess(plugin.id)))
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(profileService.updateProfile(anyOrNull())).thenReturn(Mono.just(profile))

        val returned = accessService.removeAccess(plugin.id, UUID.randomUUID())

        verify(profileService, times(1)).getProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(profile.id)
                assertThat(it.email).isEqualTo(profile.email)
            }
            .verifyComplete()
    }
}