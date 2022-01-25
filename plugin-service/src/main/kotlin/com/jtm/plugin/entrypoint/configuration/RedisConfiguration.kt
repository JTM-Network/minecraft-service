package com.jtm.plugin.entrypoint.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory

@Configuration
open class RedisConfiguration {

    @Value("\${redis.host:localhost}")
    lateinit var host: String

    @Value("\${redis.port:6379}")
    var port: Int = 0

    @Bean
    open fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }
}