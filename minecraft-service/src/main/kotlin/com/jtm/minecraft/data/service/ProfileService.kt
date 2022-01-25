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

    /**
     * Insert a profile
     *
     * @param request - the http client request
     * @throws InvalidJwtToken - if the Authorization header is null or empty or,
     *                           if the token is invalid or, if the account id/email
     *                           is not provided inside the token
     * @throws ProfileAlreadyExists - if the profile has already been found
     * @return the profile
     */
    fun insertProfile(request: ServerHttpRequest): Mono<Profile> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val id = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        val email = tokenProvider.getAccountEmail(token) ?: return Mono.error { InvalidJwtToken() }
        return profileRepository.findById(id)
            .flatMap<Profile?> { Mono.defer { Mono.error(ProfileAlreadyExists()) } }
            .switchIfEmpty(Mono.defer { profileRepository.save(Profile(id = id, email = email)) })
    }

    /**
     * Update the profile
     *
     * @param profile - the profile to be updated with
     * @throws ProfileNotFound - if the profile has not been found using the id
     * @return the profile
     */
    fun updateProfile(profile: Profile): Mono<Profile> {
        return profileRepository.findById(profile.id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap { profileRepository.save(profile) }
    }

    /**
     * Get the profile from the id
     *
     * @param id - the identifier
     * @throws ProfileNotFound - if the profile is not found
     * @return the profile
     */
    fun getProfile(id: UUID): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
    }

    /**
     * Get the profile using the Authorization header, if the profile
     * is not found. Insert a new one.
     *
     * @param request - the http client request
     * @throws InvalidJwtToken - if the Authorization header is null or empty or,
     *                           if the token is invalid or account id from token
     *                           is null or empty
     * @return the profile
     *
     */
    fun getProfileByBearer(request: ServerHttpRequest): Mono<Profile> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error { InvalidJwtToken() }
        val token = tokenProvider.resolveToken(bearer)
        val id = tokenProvider.getAccountId(token) ?: return Mono.error { InvalidJwtToken() }
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { insertProfile(request) })
    }

    /**
     * Get all profiles
     *
     * @return the list of profiles
     */
    fun getProfiles(): Flux<Profile> {
        return profileRepository.findAll()
    }

    /**
     * Delete a profile using the identifier
     *
     * @param id - the profile identifier
     * @throws ProfileNotFound - if the profile has not been found by identifier
     * @return the profile
     */
    fun deleteProfile(id: UUID): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap { profileRepository.delete(it).thenReturn(it) }
    }
}