package com.jtm.minecraft.data.proxy

import com.jtm.minecraft.core.domain.model.AccountInfo
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import reactor.core.publisher.Mono

@Component
@FeignClient("account")
interface AccountProxy {

    @RequestMapping(method = [RequestMethod.GET], value = ["/auth/me"])
    fun getAccount(@RequestHeader("Authorization") bearer: String): Mono<AccountInfo>
}