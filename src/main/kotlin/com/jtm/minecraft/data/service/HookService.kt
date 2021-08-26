package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.exceptions.FailedDeserialization
import com.jtm.minecraft.core.domain.exceptions.InvalidPaymentIntent
import com.jtm.minecraft.core.domain.exceptions.InvalidSignature
import com.jtm.minecraft.core.util.UtilString
import com.jtm.minecraft.data.service.plugin.AccessService
import com.stripe.model.Event
import com.stripe.model.PaymentIntent
import com.stripe.net.Webhook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class HookService @Autowired constructor(private val accessService: AccessService) {

    @Value("\${security.webhook.plugin:hook-secret:hook-secret}")
    lateinit var hookSecret: String

    fun addAccess(event: Event): Mono<Void> {
        val dataObjectDeserializer = event.dataObjectDeserializer
        if (!dataObjectDeserializer.`object`.isPresent) return Mono.error { FailedDeserialization() }
        val stripeObject = dataObjectDeserializer.`object`.get()
        val intent = stripeObject as PaymentIntent
        val accountId = UUID.fromString(intent.metadata["accountId"]) ?: return Mono.error { InvalidPaymentIntent() }
        val plugins = intent.metadata["plugins"] ?: return Mono.error { InvalidPaymentIntent() }
        val pluginIds = UtilString.stringToPlugins(plugins)
        return accessService.addPremiumAccess(accountId, pluginIds)
            .then()
    }
}