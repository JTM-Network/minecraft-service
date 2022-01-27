package com.jtm.profile.data.service

import com.jtm.profile.core.domain.entity.Profile
import com.jtm.profile.core.domain.exceptions.FailedFetchingClient
import com.jtm.profile.core.domain.exceptions.ProfileAlreadyBanned
import com.jtm.profile.core.domain.exceptions.ProfileNotBanned
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
     * This will ban a user, aimed at policing bad actors trying to abuse the
     * system, or for breaking a rule provided in the terms.
     *
     * @param id        the user id
     * @return          the user profile
     * @see             Profile
     * @throws ProfileNotFound if the user profile is not found.
     * @throws ProfileAlreadyBanned if the user is already banned.
     */
    fun banProfile(id: String): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (it.banned) return@flatMap Mono.error(ProfileAlreadyBanned())
                profileRepository.save(it.ban())
            }
    }

    /**
     * This will unban a user, in case of accidental bans.
     *
     * @param id        the user id
     * @return          the user profile
     * @see             Profile
     * @throws ProfileNotFound if the user profile is not found.
     * @throws ProfileNotBanned if the user is not banned.
     */
    fun unbanProfile(id: String): Mono<Profile> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (!it.banned) return@flatMap Mono.error(ProfileNotBanned())
                profileRepository.save(it.unban())
            }
    }
}