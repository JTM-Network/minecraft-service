package com.jtm.minecraft

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
open class MinecraftApplication

fun main(args: Array<String>) {
    runApplication<MinecraftApplication>(*args)
}