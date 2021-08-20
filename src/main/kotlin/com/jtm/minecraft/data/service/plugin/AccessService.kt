package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.entity.Profile
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

    fun addAccess(id: UUID, request: ServerHttpRequest): Mono<Void> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        return pluginService.getPlugin(id)
            .flatMap {
                profileService.getProfile(accountId)
                    .flatMap { profile ->
                        if (profile.isAuthenticated(it.id)) return@flatMap Mono.error { ProfileAlreadyHasAccess() }
                        profileService.updateProfile(profile.addAccess(it.id))
                    }.then()
            }
    }

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

    fun removeAccess(pluginId: UUID, accountId: UUID): Mono<Profile> {
        return profileService.getProfile(accountId)
            .flatMap { profile ->
                if (!profile.isAuthenticated(pluginId)) return@flatMap Mono.error { ProfileNoAccess() }
                pluginService.getPlugin(pluginId)
                    .flatMap { profileService.updateProfile(profile.removeAccess(it.id)) }
            }
    }
}