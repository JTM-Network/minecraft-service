package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.core.domain.exceptions.FailedPaymentIntent
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.domain.model.PluginIntent
import com.jtm.minecraft.core.usecase.stripe.StripeProvider
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.ProfileService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyArray
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
class IntentServiceTest {

    private val stripeProvider: StripeProvider = mock()
    private val tokenProvider: AccountTokenProvider = mock()
    private val profileService: ProfileService = mock()
    private val intentService = IntentService(stripeProvider, tokenProvider, profileService)

    private val request: ServerHttpRequest = mock()
    private val headers: HttpHeaders = mock()

    private val profile = Profile(email = "test@gmail.com")
    private val intent = PluginIntent(20.0, "USD", listOf(UUID.randomUUID()))

    @Before
    fun setup() {
        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("Bearer test")
    }

    @Test
    fun createIntent_thenAccountIdInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = intentService.createIntent(request, intent)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun createIntent_thenFailedPaymentIntent() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))
        `when`(stripeProvider.createPaymentIntent(anyDouble(), anyString(), anyOrNull(), anyArray())).thenReturn(null)

        val returned = intentService.createIntent(request, intent)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(profileService, times(1)).getProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)

        StepVerifier.create(returned)
            .expectError(FailedPaymentIntent::class.java)
            .verify()
    }

    @Test
    fun createIntentTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))
        `when`(stripeProvider.createPaymentIntent(anyDouble(), anyString(), anyOrNull(), anyArray())).thenReturn("secret")

        val returned = intentService.createIntent(request, intent)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(profileService, times(1)).getProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)

        StepVerifier.create(returned)
            .assertNext { assertThat(it).isEqualTo("secret") }
            .verifyComplete()
    }
}