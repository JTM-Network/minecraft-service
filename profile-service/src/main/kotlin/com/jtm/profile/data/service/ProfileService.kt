package com.jtm.profile.data.service

import com.jtm.profile.core.domain.entity.Profile
import com.jtm.profile.core.domain.exceptions.FailedFetchingClient
import com.jtm.profile.core.domain.exceptions.ProfileNotFound
import com.jtm.profile.core.usecase.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProfileService @Autowired constructor(private val profileRepository: ProfileRepository) {

    /**
     * This will get the CLIENT_ID header from the request which has been forwarded from the
     * gateway, which has been extracted from the bearer access token. If the profile has not
     * been found it will create a new one and return it.
     *
     * @param request       the http request
     * @return              the user profile
     * @see                 Profile
     */
    fun getProfile(request: ServerHttpRequest): Mono<Profile> {
        val clientId = request.headers.getFirst("CLIENT_ID") ?: return Mono.error(FailedFetchingClient())
        return profileRepository.findById(clientId)
            .switchIfEmpty(Mono.defer { profileRepository.save(Profile(clientId)) })
    }

    /**
     * This will allow a user to be banned, aimed at policing bad actors trying to abuse the
     * system, or for breaking a rule provided in the terms.
     *
     * @param id        the user id
     * @return          the user profile
     * @see             Profile
     * @throws ProfileNotFound if the user id is not found.
     */
    fun banProfile(id: String): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap { profileRepository.save(it.addBan()) }
    }
}