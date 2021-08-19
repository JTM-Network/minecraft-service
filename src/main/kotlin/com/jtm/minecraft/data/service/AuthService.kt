package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.entity.BlacklistToken
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginNotFound
import com.jtm.minecraft.core.domain.exceptions.token.BlacklistTokenFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.domain.model.AuthToken
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AuthService @Autowired constructor(private val tokenProvider: AccountTokenProvider,
                                         private val pluginService: PluginService,
                                         private val tokenService: BlacklistTokenService) {

    @Value("\${sentry.plugin:pluginDsn}")
    var pluginDsn: String = ""

    fun authenticate(request: ServerHttpRequest, plugin: String): Mono<AuthToken> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val id = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        val email = tokenProvider.getAccountEmail(token) ?: return Mono.error { InvalidJwtToken() }
        return pluginService.getPluginByName(plugin)
            .switchIfEmpty(Mono.defer { Mono.error { PluginNotFound() } })
            .flatMap {
                val pluginToken = tokenProvider.createPluginToken(id, email, it.id)
                return@flatMap Mono.just(AuthToken(pluginToken, pluginDsn))
            }
    }

    fun blacklistToken(request: ServerHttpRequest): Mono<BlacklistToken> {
        val bearer = request.headers.getFirst("PLUGIN_AUTHORIZATION") ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        return tokenService.isBlacklisted(token)
            .flatMap {
                if (it) return@flatMap Mono.error(BlacklistTokenFound())
                return@flatMap tokenService.insertToken(token)
            }
    }
}