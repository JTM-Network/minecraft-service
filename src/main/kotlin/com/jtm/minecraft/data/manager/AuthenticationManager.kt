package com.jtm.minecraft.data.manager

import com.jtm.minecraft.core.domain.exceptions.plugin.PluginIdNotFound
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginUnauthorized
import com.jtm.minecraft.core.domain.exceptions.token.BlacklistTokenFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.BlacklistTokenService
import com.jtm.minecraft.data.service.ProfileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class AuthenticationManager @Autowired constructor(private val tokenProvider: AccountTokenProvider,
                                                   private val tokenService: BlacklistTokenService,
                                                   private val profileService: ProfileService): ReactiveAuthenticationManager {

    /**
     * Authenticate the token {@link Authentication#principal} and the plugin id {@link Authentication#credentials},
     * using the token we get the account/profile id. We check if the token is blacklisted, we check if the account,
     * has access to the plugin.
     *
     * @param authentication - the details of the authentication
     * @throws InvalidJwtToken - if the principal is null or if the plugin account id is null
     * @throws PluginIdNotFound - if the credentials is null or empty
     * @throws BlacklistTokenFound - if the token is blacklisted
     * @throws PluginUnauthorized - if the account does not have access
     *
     */
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.principal ?: return Mono.error { InvalidJwtToken() }
        val plugin = authentication.credentials ?: return Mono.error { PluginIdNotFound() }
        val pluginId = UUID.fromString(plugin.toString())
        val id = tokenProvider.getPluginAccountId(token.toString()) ?: return Mono.error { InvalidJwtToken() }

        return tokenService.isBlacklisted(token.toString())
            .flatMap {
                if (it) return@flatMap Mono.error(BlacklistTokenFound())
                return@flatMap profileService.getProfile(id)
                    .flatMap { profile ->
                        if (!profile.isAuthenticated(pluginId)) return@flatMap Mono.error { PluginUnauthorized() }
                        val auth = UsernamePasswordAuthenticationToken(token, plugin, listOf())
                        SecurityContextHolder.getContext().authentication = authentication
                        return@flatMap Mono.just(auth)
                    }
            }
    }
}