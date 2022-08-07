package com.jtm.profile

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class ProfileServiceApplication

fun main(args: Array<String>) {
    runApplication<ProfileServiceApplication>(*args)
}