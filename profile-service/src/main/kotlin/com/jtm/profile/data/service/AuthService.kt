package com.jtm.profile.data.service

import com.jtm.profile.core.domain.exceptions.ProfileNotFound
import com.jtm.profile.core.domain.exceptions.ProfileUnauthorized
import com.jtm.profile.core.usecase.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class AuthService @Autowired constructor(private val profileRepository: ProfileRepository) {

    /**
     * This will check if the profile is authorized to use the plugin.
     *
     * @param id        the profile identifier
     * @param plugin    the plugin identifier
     * @return          empty publisher
     * @see             Void
     * @throws ProfileUnauthorized if the profile is not authorized to use the plugin.
     */
    fun isAuthorized(id: String, plugin: UUID): Mono<Void> {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap {
                if (!it.hasPlugin(plugin)) return@flatMap Mono.error(ProfileUnauthorized())
                Mono.empty()
            }
    }
}