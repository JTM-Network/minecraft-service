package com.jtm.minecraft

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
open class MinecraftService

fun main(args: Array<String>) {
    SpringApplication.run(MinecraftService::class.java, *args)
}