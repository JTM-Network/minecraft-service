package com.jtm.minecraft.data.proxy

import com.jtm.minecraft.core.domain.model.AccountInfo
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import reactor.core.publisher.Mono

@FeignClient("account")
interface AccountProxy {

    @GetMapping("/auth/me")
    fun getAccount(request: ServerHttpRequest): Mono<AccountInfo>
}