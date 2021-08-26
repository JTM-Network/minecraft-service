package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.exceptions.InvalidPaymentIntent
import com.jtm.minecraft.core.util.UtilString
import com.jtm.minecraft.data.service.plugin.AccessService
import com.stripe.model.Event
import com.stripe.model.PaymentIntent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class HookService @Autowired constructor(private val accessService: AccessService) {

    fun addAccess(event: Event): Mono<Void> {
        val dataObjectDeserializer = event.dataObjectDeserializer
        val stripeObject = dataObjectDeserializer.`object`.get()
        val intent = stripeObject as PaymentIntent
        val accountId = UUID.fromString(intent.metadata["accountId"]) ?: return Mono.error { InvalidPaymentIntent() }
        val plugins = intent.metadata["plugins"] ?: return Mono.error { InvalidPaymentIntent() }
        val pluginIds = UtilString.stringToPlugins(plugins)
        return accessService.addPremiumAccess(accountId, pluginIds)
            .then()
    }
}