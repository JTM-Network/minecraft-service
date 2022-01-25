package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginIsPremium
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginUnauthorized
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileAlreadyHasAccess
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileNoAccess
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.PluginService
import com.jtm.minecraft.data.service.ProfileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class AccessService @Autowired constructor(private val profileService: ProfileService,
                                           private val pluginService: PluginService,
                                           private val tokenProvider: AccountTokenProvider) {

    /**
     * Grants the user access to a plugin using the plugin's identifier
     *
     * @param id - the plugin identifier
     * @param request - the client side request
     * @throws InvalidJwtToken - if the Authorization header is empty or null, or
     *                           if the token is invalid or has no account id provided
     * @throws ProfileAlreadyHasAccess - if the user already has access to the plugin
     * @return an empty Mono publisher if successful
     */
    fun addAccess(id: UUID, request: ServerHttpRequest): Mono<Void> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        return pluginService.getPlugin(id)
            .flatMap {
                if (it.premium) return@flatMap Mono.error { PluginIsPremium() }
                profileService.getProfile(accountId)
                    .flatMap { profile ->
                        if (profile.isAuthenticated(it.id)) return@flatMap Mono.error { ProfileAlreadyHasAccess() }
                        profileService.updateProfile(profile.addAccess(it.id)).then()
                    }

            }
    }

    /**
     * Grants the user access to a premium plugin
     *
     * @param accountId - the accounts identifier
     * @param plugins - the plugins to be authenticated
     * @return an empty Mono publisher if successful
     */
    fun addPremiumAccess(accountId: UUID, plugins: Array<UUID>): Mono<Profile> {
        return profileService.getProfile(accountId)
            .flatMap { profileService.updateProfile(it.addAccess(plugins))}
    }

    /**
     * Returns if the user has access to the plugin using the name.
     *
     * @param name - the name of the plugin
     * @param request - the http client request
     * @throws InvalidJwtToken - if the Authorization header is empty or null, or
     *                           if the token is invalid or has no account id provided
     * @throws PluginUnauthorized - if the user doesn't have access to the plugin
     * @return an empty Mono publisher if user has access
     */
    fun hasAccess(name: String, request: ServerHttpRequest): Mono<Void> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        return pluginService.getPluginByName(name)
            .flatMap { plugin ->
                profileService.getProfile(accountId)
                    .flatMap {
                        if (!it.isAuthenticated(plugin.id)) return@flatMap Mono.error { PluginUnauthorized() }
                        Mono.empty()
                    }
            }
    }

    /**
     * Removes plugin access from a user using the plugin's identifier
     *
     * @param pluginId - the plugin identifier
     * @param accountId - the users account identifier
     * @throws ProfileNoAccess - if the user already has no access to the plugin
     * @return - the profile the access has been removed from.
     */
    fun removeAccess(pluginId: UUID, accountId: UUID): Mono<Profile> {
        return profileService.getProfile(accountId)
            .flatMap { profile ->
                if (!profile.isAuthenticated(pluginId)) return@flatMap Mono.error { ProfileNoAccess() }
                pluginService.getPlugin(pluginId)
                    .flatMap { profileService.updateProfile(profile.removeAccess(it.id)) }
            }
    }
}