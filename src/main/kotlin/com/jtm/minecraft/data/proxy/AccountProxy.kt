package com.jtm.minecraft.data.proxy

import com.jtm.minecraft.core.domain.model.AccountInfo
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import reactor.core.publisher.Mono

@Component
@FeignClient("account")
interface AccountProxy {

    @GetMapping("/auth/me")
    fun getAccount(@RequestHeader("Authorization") bearer: String): AccountInfo
}