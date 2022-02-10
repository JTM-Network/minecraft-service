package com.jtm.profile.data.service

import com.jtm.profile.core.domain.dto.AccessDto
import com.jtm.profile.core.domain.entity.Profile
import com.jtm.profile.core.domain.exceptions.ProfileNotFound
import com.jtm.profile.core.usecase.repository.ProfileRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class AccessServiceTest {

    private val profileRepository: ProfileRepository = mock()
    private val accessService = AccessService(profileRepository)
    private val profile = Profile(id = UUID.randomUUID().toString())
    private val dto = AccessDto(UUID.randomUUID().toString(), listOf(UUID.randomUUID()))

    @Test
    fun addAccess_thenProfileNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = accessService.addAccess(dto)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun addAccess() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(profile))
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(profile))

        val returned = accessService.addAccess(dto)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .verifyComplete()
    }
}