package com.jtm.minecraft.core.util

import com.jtm.minecraft.MinecraftApplication
import io.sentry.Sentry
import io.sentry.SentryLevel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Logging {
    private val logger = LoggerFactory.getLogger(MinecraftApplication::class.java)

    fun info(msg: String, sentry: Boolean) {
        logger.info(msg)
        if (sentry) Sentry.captureMessage(msg, SentryLevel.INFO)
    }

    fun warn(msg: String, sentry: Boolean) {
        logger.warn(msg)
        if (sentry) Sentry.captureMessage(msg, SentryLevel.WARNING)
    }

    fun debug(msg: String, sentry: Boolean) {
        logger.debug(msg)
        if (sentry) Sentry.captureMessage(msg, SentryLevel.DEBUG)
    }

    fun error(msg: String, sentry: Boolean) {
        logger.error(msg)
        if (sentry) Sentry.captureMessage(msg, SentryLevel.ERROR)
    }

    fun error(throwable: Throwable) {
        logger.error("Error: ${throwable.message}")
        Sentry.captureException(throwable)
    }
}