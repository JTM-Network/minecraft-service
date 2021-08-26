package com.jtm.minecraft.core.usecase.stripe

import com.jtm.minecraft.core.util.Logging
import com.jtm.minecraft.core.util.UtilString
import com.stripe.Stripe
import com.stripe.exception.StripeException
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

@Component
class StripeProvider @Autowired constructor(private val logging: Logging) {

    @Value("\${security.stripe.secret-key:secretKey}")
    lateinit var secretKey: String

    @PostConstruct
    fun init() {
        Stripe.apiKey = secretKey
    }

    fun createPaymentIntent(amount: Double, currency: String, accountId: UUID, plugins: Array<UUID>): String? {
        return try {
            val longAmount = (amount * 100).toLong()
            val params = PaymentIntentCreateParams.builder()
                .setCurrency(currency.uppercase(Locale.getDefault()))
                .setAmount(longAmount)
                .putAllMetadata(mapOf("accountId" to accountId.toString(), "plugins" to UtilString.pluginsToString(plugins)))
                .build()

            val intent = PaymentIntent.create(params)
            intent.clientSecret
        } catch (ex: StripeException) {
            logging.error(ex)
            null
        }
    }
}