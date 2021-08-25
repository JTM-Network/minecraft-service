package com.jtm.minecraft.data.security

import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.manager.AuthenticationManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityContextRepository @Autowired constructor(private val authenticationManager: AuthenticationManager,
                                                       private val tokenProvider: AccountTokenProvider): ServerSecurityContextRepository {

    override fun save(exchange: ServerWebExchange?, context: SecurityContext?): Mono<Void> = Mono.empty()

    /**
     * Create Security Context converted from authenticating using
     * {@link AuthenticationManager#authenticate} method, extracting the
     * tokens from the exchange interaction to be used to authenticate
     * the request.
     *
     * @param exchange - the web exchange
     * @return the security context
     */
    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val request = exchange.request
        val pluginBearer = request.headers.getFirst("PLUGIN_AUTHORIZATION") ?: return Mono.empty()
        val pluginToken = tokenProvider.resolveToken(pluginBearer)
        if (pluginToken.isEmpty()) return Mono.empty()
        val plugin = tokenProvider.getPluginId(pluginToken) ?: return Mono.empty()

        val auth = UsernamePasswordAuthenticationToken(pluginToken, plugin)
        return authenticationManager.authenticate(auth).map { SecurityContextImpl(it) }
    }
}