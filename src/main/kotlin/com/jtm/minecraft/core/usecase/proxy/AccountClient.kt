package com.jtm.minecraft.core.usecase.proxy

import com.jtm.minecraft.core.domain.dto.AccountDto
import org.springframework.cloud.square.retrofit.core.RetrofitClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import reactor.core.publisher.Mono
import retrofit2.Call

@Component
@RetrofitClient("account")
interface AccountClient {

    @GetMapping("/auth/me")
    fun whoami(@RequestHeader("Authorization") bearer: String): Call<AccountDto>
}