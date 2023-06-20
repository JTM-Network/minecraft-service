package com.jtm.plugin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class PluginServiceApplication

fun main(args: Array<String>) {
    runApplication<PluginServiceApplication>(*args)
}