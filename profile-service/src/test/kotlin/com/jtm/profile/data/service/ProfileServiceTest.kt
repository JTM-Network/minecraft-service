package com.jtm.profile.data.service

import com.jtm.profile.core.domain.entity.Profile
import com.jtm.profile.core.domain.exceptions.FailedFetchingClient
import com.jtm.profile.core.domain.exceptions.ProfileAlreadyBanned
import com.jtm.profile.core.domain.exceptions.ProfileNotBanned
import com.jtm.profile.core.domain.exceptions.ProfileNotFound
import com.jtm.profile.core.usecase.repository.ProfileRepository
import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
class ProfileServiceTest {

    private val profileRepository: ProfileRepository = mock()
    private val profileService = ProfileService(profileRepository)
    private val profile = Profile("user_id")
    private val request: ServerHttpRequest = mock()
    private val headers: HttpHeaders = mock()

    @Before
    fun setup() {
        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst("CLIENT_ID")).thenReturn("user_id")
    }

    @Test
    fun getProfile_thenFailedFetchingClient() {
        `when`(headers.getFirst("CLIENT_ID")).thenReturn(null)

        val returned = profileService.getProfile(request)

        verify(request, times(1)).headers
        verifyNoMoreInteractions(request)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(FailedFetchingClient::class.java)
            .verify()
    }

    @Test
    fun getProfile_thenIfEmpty() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(profile))

        val returned = profileService.getProfile(request)

        verify(request, times(1)).headers
        verifyNoMoreInteractions(request)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.id).isEqualTo("user_id") }
            .verifyComplete()
    }

    @Test
    fun getProfile() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(profile))

        val returned = profileService.getProfile(request)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.id).isEqualTo("user_id") }
            .verifyComplete()
    }

    @Test
    fun banProfile_thenNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = profileService.banProfile("user_id")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun banProfile_thenAlreadyBanned() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(profile.ban()))

        val returned = profileService.banProfile("user_id")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileAlreadyBanned::class.java)
            .verify()
    }

    @Test
    fun banProfile() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(profile))
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(profile))

        val returned = profileService.banProfile("user_id")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo("user_id")
                assertTrue(it.banned)
            }
            .verifyComplete()
    }

    @Test
    fun unbanProfile_thenNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = profileService.unbanProfile("user_id")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun unbanProfile_thenNotBanned() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(profile))

        val returned = profileService.unbanProfile("user_id")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotBanned::class.java)
            .verify()
    }

    @Test
    fun unbanProfile() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(profile.ban()))
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(profile))

        val returned = profileService.unbanProfile("user_id")

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.id).isEqualTo("user_id") }
            .verifyComplete()
    }
}