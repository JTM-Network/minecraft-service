package com.jtm.version

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class VersionApplication

fun main(args: Array<String>) {
    runApplication<VersionApplication>(*args)
}