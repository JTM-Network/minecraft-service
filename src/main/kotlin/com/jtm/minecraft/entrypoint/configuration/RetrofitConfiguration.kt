package com.jtm.minecraft.entrypoint.configuration

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.square.retrofit.EnableRetrofitClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableRetrofitClients
open class RetrofitConfiguration {

    @Bean
    @LoadBalanced
    fun okHttpClientBuilder(): WebClient.Builder {
        return WebClient.builder()
    }
}