package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.exceptions.InvalidJwtToken
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/test")
class TestController @Autowired constructor(private val accountTokenProvider: AccountTokenProvider) {

    @GetMapping("/account")
    fun account(request: ServerHttpRequest): Mono<UUID> {
        val bearer = request.headers.getFirst("Authorization") ?: return Mono.error(InvalidJwtToken())
        val token = accountTokenProvider.resolveToken(bearer)
        return Mono.just(accountTokenProvider.getAccountId(token))
    }
}