package com.jtm.minecraft.entrypoint.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.jtm.minecraft.core.usecase.stripe.StripeEventDeserializer
import com.stripe.model.Event
import io.sentry.Sentry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.annotation.PostConstruct


@Configuration
open class AppConfiguration {

    @Value("\${sentry.service:dsn}")
    lateinit var dsn: String

    @PostConstruct
    fun init() {
        Sentry.init {
            it.dsn = dsn
            it.setDebug(true)
        }
    }

    @Bean
    @Primary
    open fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        val module = SimpleModule()
        module.addDeserializer(Event::class.java, StripeEventDeserializer(mapper))
        return mapper.registerModule(module)
    }
}