package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.exceptions.FailedPaymentIntent
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.domain.model.PluginIntent
import com.jtm.minecraft.core.usecase.stripe.StripeProvider
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.ProfileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class IntentService @Autowired constructor(private val stripeProvider: StripeProvider, private val tokenProvider: AccountTokenProvider, private val profileService: ProfileService) {

    fun createIntent(request: ServerHttpRequest, intent: PluginIntent): Mono<String> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        return profileService.getProfile(accountId)
            .flatMap {
                val secret = stripeProvider.createPaymentIntent(intent.total, intent.currency, it.id, intent.plugins.toTypedArray()) ?: return@flatMap Mono.error { FailedPaymentIntent() }
                return@flatMap Mono.just(secret)
            }
    }
}