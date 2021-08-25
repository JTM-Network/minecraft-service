package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.entity.BlacklistToken
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginNotFound
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginUnauthorized
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileNotFound
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

    /**
     * Authentication logic used to allow minecraft servers to the plugin,
     * it takes the api token from the Authorization header and parses it.
     * Checks if the account id inside the token has access to the plugin,
     * if account has access to the plugin, this will create a new token to
     * be used by the plugin to access protected endpoints using this services
     * security protocols. Also sending a sentry dsn to log errors for continuous
     * monitoring.
     *
     * TODO: Currently this does not capture multiple uses of the same account
     *       when using the plugin, so will need to add that in the future.
     *
     * @param request - the http client request
     * @param profileService - the profile service
     * @param plugin - the name of the plugin
     * @throws InvalidJwtToken - if the Authorization header returns a null or empty value, or
     *                           if the token is invalid or account id or email is not found,
     *                           inside the token
     * @throws PluginNotFound - if the plugin has not been found by name
     * @throws ProfileNotFound - if the profile has not been found by the account id
     * @throws PluginUnauthorized - if the profile does not have access to the resource
     * @return - an {@link AuthToken} which provides a JWT token and the plugin Sentry DSN
     */
    fun authenticate(request: ServerHttpRequest, profileService: ProfileService, plugin: String): Mono<AuthToken> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val id = tokenProvider.getApiAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        val email = tokenProvider.getApiAccountEmail(token) ?: return Mono.error { InvalidJwtToken() }
        return pluginService.getPluginByName(plugin)
            .switchIfEmpty(Mono.defer { Mono.error { PluginNotFound() } })
            .flatMap {
                profileService.getProfile(id)
                    .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
                    .flatMap { profile ->
                        if (!profile.isAuthenticated(it.id)) return@flatMap Mono.error(PluginUnauthorized())
                        val pluginToken = tokenProvider.createPluginToken(id, email, it.id)
                        return@flatMap Mono.just(AuthToken(pluginToken, pluginDsn))
                    }
            }
    }

    /**
     * When the minecraft server stops the plugin will release the token given using
     * {@link AuthService#authenticate} and to keep the token dead after use, we will
     * have it stored in the blacklist to keep a record.
     *
     * @param request - the http client request
     * @throws InvalidJwtToken -  if the Plugin Authorization header is empty or null
     * @throws BlacklistTokenFound - if the token has already been stored
     * @return the blacklisted token.
     */
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