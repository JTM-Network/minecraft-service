package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.entity.BlacklistToken
import com.jtm.minecraft.core.domain.exceptions.token.BlacklistTokenNotFound
import com.jtm.minecraft.core.usecase.repository.BlacklistTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BlacklistTokenService @Autowired constructor(private val tokenRepository: BlacklistTokenRepository) {

    fun insertToken(token: String): Mono<BlacklistToken> {
        return tokenRepository.findById(token)
            .switchIfEmpty(Mono.defer { tokenRepository.save(BlacklistToken(token = token)) })
    }

    fun isBlacklisted(token: String): Mono<Boolean> {
        return tokenRepository.findById(token)
            .map { true }
            .switchIfEmpty(Mono.defer { Mono.just(false) })
    }

    fun getTokens(): Flux<BlacklistToken> {
        return tokenRepository.findAll()
    }

    fun deleteToken(token: String): Mono<BlacklistToken> {
        return tokenRepository.findById(token)
            .switchIfEmpty(Mono.defer { Mono.error(BlacklistTokenNotFound()) })
            .flatMap { tokenRepository.delete(it).thenReturn(it) }
    }
}