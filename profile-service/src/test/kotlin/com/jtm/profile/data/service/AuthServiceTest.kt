package com.jtm.profile.data.service

import com.jtm.profile.core.domain.entity.Profile
import com.jtm.profile.core.domain.exceptions.ProfileNotFound
import com.jtm.profile.core.domain.exceptions.ProfileUnauthorized
import com.jtm.profile.core.usecase.repository.ProfileRepository
import org.junit.Before
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
class AuthServiceTest {

    private val profileRepository: ProfileRepository = mock()
    private val authService = AuthService(profileRepository)
    private val profile: Profile = mock()

    @Before
    fun setup() {
        `when`(profile.id).thenReturn("test")
    }

    @Test
    fun isAuthorized_thenProfileNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = authService.isAuthorized("test", UUID.randomUUID())

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun isAuthorized_thenProfileUnauthorized() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(profile))
        `when`(profile.hasPlugin(anyOrNull())).thenReturn(false)

        val returned = authService.isAuthorized("test", UUID.randomUUID())

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileUnauthorized::class.java)
            .verify()
    }

    @Test
    fun isAuthorized() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(profile))
        `when`(profile.hasPlugin(anyOrNull())).thenReturn(true)

        val returned = authService.isAuthorized("test", UUID.randomUUID())

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .verifyComplete()
    }
}