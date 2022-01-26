package com.jtm.plugin.entrypoint.controller

import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class TestController {

    @GetMapping("/test")
    fun test(request: ServerHttpRequest): Mono<String> {
        val id = request.headers.getFirst("CLIENT_ID") ?: return Mono.empty()
        return Mono.just(id)
    }
}