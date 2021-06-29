package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.exceptions.InvalidHeader
import com.jtm.minecraft.core.domain.model.AccountInfo
import com.jtm.minecraft.data.proxy.AccountProxy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/test")
class TestController @Autowired constructor(private val accountProxy: AccountProxy) {

    @GetMapping("/account")
    fun account(request: ServerHttpRequest): Mono<AccountInfo> {
        val bearer = request.headers.getFirst("Authorization") ?: return Mono.error { InvalidHeader() }
        val account = accountProxy.getAccount(bearer)
        return Mono.just(account)
    }
}