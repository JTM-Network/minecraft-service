package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.entity.BlacklistToken
import com.jtm.minecraft.core.domain.model.AuthToken
import com.jtm.minecraft.data.service.AuthService
import com.jtm.minecraft.data.service.ProfileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController @Autowired constructor(private val authService: AuthService, private val profileService: ProfileService) {

    @GetMapping("/authenticate")
    fun postAuthenticate(request: ServerHttpRequest, @RequestParam("plugin") plugin: String): Mono<AuthToken> {
        return authService.authenticate(request, profileService, plugin)
    }

    @DeleteMapping("/blacklist")
    fun blacklistToken(request: ServerHttpRequest): Mono<BlacklistToken> {
        return authService.blacklistToken(request)
    }
}