package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.exceptions.PluginNotFound
import com.jtm.minecraft.core.domain.model.AccountInfo
import com.jtm.minecraft.core.usecase.repository.PluginRepository
import com.jtm.minecraft.data.proxy.AccountProxy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TestService @Autowired constructor(private val pluginRepository: PluginRepository, @Lazy private val accountProxy: AccountProxy) {

    fun testAccount(bearer: String): Mono<AccountInfo> {
        return accountProxy.getAccount(bearer)
    }
}