package com.jtm.profile.data.service

import com.jtm.profile.core.domain.dto.AccessDto
import com.jtm.profile.core.domain.exceptions.ProfileNotFound
import com.jtm.profile.core.usecase.repository.ProfileRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AccessService @Autowired constructor(private val profileRepository: ProfileRepository) {

    fun addAccess(dto: AccessDto): Mono<Void> {
        return profileRepository.findById(dto.clientId)
            .switchIfEmpty(Mono.defer { Mono.error(ProfileNotFound()) })
            .flatMap { profileRepository.save(it.addPlugins(dto.plugins)).then() }
    }
}