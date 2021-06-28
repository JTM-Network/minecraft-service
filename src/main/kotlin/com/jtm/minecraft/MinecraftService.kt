package com.jtm.minecraft

import feign.codec.Decoder
import feign.codec.Encoder
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
open class MinecraftService

fun main(args: Array<String>) {
    SpringApplication.run(MinecraftService::class.java, *args)
}

@Bean
fun decoder(): Decoder {
    return JacksonDecoder()
}

@Bean
fun encoder(): Encoder {
    return JacksonEncoder()
}