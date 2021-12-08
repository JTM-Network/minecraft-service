package com.jtm.minecraft.entrypoint.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
open class RedisConfiguration {

    @Value("\${redis.host:localhost}")
    val host: String = ""

    @Value("\${redis.port:6379}")
    val port: Int = 0

    open fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }
}