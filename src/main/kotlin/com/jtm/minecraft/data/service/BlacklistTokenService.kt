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

    /**
     * Inserts a JWT token to be blacklisted
     *
     * @param token - the JWT token
     * @return the blacklisted token
     */
    fun insertToken(token: String): Mono<BlacklistToken> {
        return tokenRepository.findById(token)
            .switchIfEmpty(Mono.defer { tokenRepository.save(BlacklistToken(token = token)) })
    }

    /**
     * Checks to see if the token is blacklisted
     *
     * @param token - the JWT token
     * @return the value of the check
     */
    fun isBlacklisted(token: String): Mono<Boolean> {
        return tokenRepository.findById(token)
            .map { true }
            .switchIfEmpty(Mono.defer { Mono.just(false) })
    }

    /**
     * Get a list of tokens that are blacklisted
     *
     * @return the list of tokens
     */
    fun getTokens(): Flux<BlacklistToken> {
        return tokenRepository.findAll()
    }

    /**
     * Remove a token from the blacklist
     *
     * @return the token that has been removed.
     */
    fun deleteToken(token: String): Mono<BlacklistToken> {
        return tokenRepository.findById(token)
            .switchIfEmpty(Mono.defer { Mono.error(BlacklistTokenNotFound()) })
            .flatMap { tokenRepository.delete(it).thenReturn(it) }
    }
}