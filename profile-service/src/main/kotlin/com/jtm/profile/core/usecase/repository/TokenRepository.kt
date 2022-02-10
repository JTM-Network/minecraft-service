package com.jtm.profile.core.usecase.repository

import com.jtm.profile.core.domain.entity.Token
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface TokenRepository: ReactiveMongoRepository<Token, UUID> {

    fun findByToken(token: String): Mono<Token>

    fun findByClientId(clientId: String): Flux<Token>
}