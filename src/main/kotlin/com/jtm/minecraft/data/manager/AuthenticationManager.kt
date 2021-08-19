package com.jtm.minecraft.data.manager

import com.jtm.minecraft.core.domain.exceptions.plugin.PluginHeaderMissing
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginUnauthorized
import com.jtm.minecraft.core.domain.exceptions.token.BlacklistTokenFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.core.util.Logging
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
                                                   private val profileService: ProfileService,
                                                   private val logging: Logging): ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.principal ?: return Mono.error { InvalidJwtToken() }
        val plugin = authentication.credentials ?: return Mono.error { PluginHeaderMissing() }
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