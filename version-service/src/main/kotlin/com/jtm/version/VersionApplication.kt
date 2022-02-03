package com.jtm.version

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
open class VersionApplication

fun main(args: Array<String>) {
    runApplication<VersionApplication>(*args)
}