package com.jtm.minecraft.entrypoint.configuration

import io.sentry.Sentry
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
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
}