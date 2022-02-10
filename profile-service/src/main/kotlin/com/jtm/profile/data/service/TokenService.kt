package com.jtm.profile.data.service

import com.jtm.profile.core.domain.entity.Token
import com.jtm.profile.core.domain.exceptions.ClientIdNotFound
import com.jtm.profile.core.domain.exceptions.ProfileNotFound
import com.jtm.profile.core.domain.exceptions.token.TokenNotFound
import com.jtm.profile.core.usecase.provider.TokenProvider
import com.jtm.profile.core.usecase.repository.ProfileRepository
import com.jtm.profile.core.usecase.repository.TokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class TokenService @Autowired constructor(private val tokenRepository: TokenRepository,
                                          private val profileRepository: ProfileRepository,
                                          private val tokenProvider: TokenProvider) {

    fun generateToken(request: ServerHttpRequest): Mono<Token> {
        val id = request.headers.getFirst("CLIENT_ID") ?: return Mono.error { ClientIdNotFound() }
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                val token = tokenProvider.createToken(id)
                tokenRepository.save(Token(token = token, clientId = id))
            }
    }

    fun getTokenById(id: UUID): Mono<Token> {
        return tokenRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(TokenNotFound()) })
    }

    fun getToken(token: String): Mono<Token> {
        return tokenRepository.findByToken(token)
            .switchIfEmpty(Mono.defer { Mono.error(TokenNotFound()) })
    }

    fun getTokens(): Flux<Token> {
        return tokenRepository.findAll()
    }

    fun getTokensByAccount(request: ServerHttpRequest): Flux<Token> {
        val id = request.headers.getFirst("CLIENT_ID") ?: return Flux.error(ClientIdNotFound())
        return tokenRepository.findByClientId(id)
    }

    fun getTokensByAccountId(accountId: String): Flux<Token> {
        return tokenRepository.findByClientId(accountId)
    }

    fun removeToken(id: UUID): Mono<Token> {
        return tokenRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(TokenNotFound()) })
            .flatMap { tokenRepository.delete(it).thenReturn(it) }
    }
}