package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileAlreadyExists
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.repository.ProfileRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class ProfileService @Autowired constructor(private val profileRepository: ProfileRepository,
                                            private val tokenProvider: AccountTokenProvider) {

    fun insertProfile(request: ServerHttpRequest): Mono<Profile> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        if (token.isEmpty()) return Mono.error { InvalidJwtToken() }
        val id = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        val email = tokenProvider.getAccountEmail(token) ?: return Mono.error { InvalidJwtToken() }
        return profileRepository.findById(id)
            .flatMap<Profile?> { Mono.defer { Mono.error(ProfileAlreadyExists()) } }
            .switchIfEmpty(Mono.defer { profileRepository.save(Profile(id = id, email = email)) })
    }

    fun getProfile(id: UUID): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
    }

    fun getProfileByBearer(request: ServerHttpRequest): Mono<Profile> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        if (token.isEmpty()) return Mono.error { InvalidJwtToken() }
        val id = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
    }

    fun getProfiles(): Flux<Profile> {
        return profileRepository.findAll()
    }

    fun deleteProfile(id: UUID): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap { profileRepository.delete(it).thenReturn(it) }
    }
}