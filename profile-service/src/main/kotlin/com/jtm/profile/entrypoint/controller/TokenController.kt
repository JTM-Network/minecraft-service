package com.jtm.profile.entrypoint.controller

import com.jtm.profile.core.domain.entity.Token
import com.jtm.profile.data.service.TokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/token")
class TokenController @Autowired constructor(private val tokenService: TokenService) {

    @GetMapping("/generate")
    fun generateToken(request: ServerHttpRequest): Mono<Token> {
        return tokenService.generateToken(request)
    }

    @GetMapping("/{id}")
    fun getTokenById(@PathVariable id: UUID): Mono<Token> {
        return tokenService.getTokenById(id)
    }

    @GetMapping
    fun getToken(@RequestParam(value = "value") token: String): Mono<Token> {
        return tokenService.getToken(token)
    }

    @GetMapping("/all")
    fun getTokens(): Flux<Token> {
        return tokenService.getTokens()
    }

    @GetMapping("/account")
    fun getTokensByAccount(request: ServerHttpRequest): Flux<Token> {
        return tokenService.getTokensByAccount(request)
    }

    @GetMapping("/account/{id}")
    fun getTokensByAccountId(@PathVariable id: String): Flux<Token> {
        return tokenService.getTokensByAccountId(id)
    }

    @DeleteMapping("/{id}")
    fun deleteToken(@PathVariable id: UUID): Mono<Token> {
        return tokenService.removeToken(id)
    }
}