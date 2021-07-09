package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.dto.AccountDto
import com.jtm.minecraft.core.domain.exceptions.InvalidJwtToken
import com.jtm.minecraft.core.usecase.proxy.AccountClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/test")
class AccountController @Autowired constructor(val client: AccountClient) {

    @GetMapping("/me")
    fun getMe(request: ServerHttpRequest): Mono<AccountDto> {
        val bearer = request.headers.getFirst("Authorization") ?: return Mono.error { InvalidJwtToken() }
        return client.whoami(bearer)
    }
}