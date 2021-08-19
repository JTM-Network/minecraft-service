package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileAlreadyExists
import com.jtm.minecraft.core.domain.exceptions.profile.ProfileNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.repository.ProfileRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class ProfileServiceTest {

    private val profileRepository: ProfileRepository = mock()
    private val tokenProvider: AccountTokenProvider = mock()
    private val profileService = ProfileService(profileRepository, tokenProvider)
    private val created = Profile(email = "test@gmail.com")
    private val request: ServerHttpRequest = mock()
    private val headers: HttpHeaders = mock()

    @Before
    fun setup() {
        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("Bearer test")
    }

    @Test
    fun insertProfile_thenResolveTokenInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("")

        val returned = profileService.insertProfile(request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun insertProfile_thenAccountIdInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = profileService.insertProfile(request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun insertProfile_thenAccountEmailInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(tokenProvider.getAccountEmail(anyString())).thenReturn(null)

        val returned = profileService.insertProfile(request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verify(tokenProvider, times(1)).getAccountEmail(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun insertProfile_thenProfileAlreadyExists() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(tokenProvider.getAccountEmail(anyString())).thenReturn("test@gmail.com")
        `when`(profileRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))

        val returned = profileService.insertProfile(request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verify(tokenProvider, times(1)).getAccountEmail(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(profileRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileAlreadyExists::class.java)
            .verify()
    }

    @Test
    fun insertProfileTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(tokenProvider.getAccountEmail(anyString())).thenReturn("test@gmail.com")

        `when`(profileRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())
        `when`(profileRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = profileService.insertProfile(request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verify(tokenProvider, times(1)).getAccountEmail(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(profileRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(created.id)
                assertThat(it.email).isEqualTo(created.email)
            }
            .verifyComplete()
    }

    @Test
    fun getProfile_thenNotFound() {
        `when`(profileRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = profileService.getProfile(UUID.randomUUID())

        verify(profileRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun getProfileTest() {
        `when`(profileRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))

        val returned = profileService.getProfile(UUID.randomUUID())

        verify(profileRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(created.id)
                assertThat(it.email).isEqualTo(created.email)
            }
            .verifyComplete()
    }

    @Test
    fun getProfileByBearer_thenResolveTokenEmpty() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("")

        val returned = profileService.getProfileByBearer(request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun getProfileByBearer_thenAccountIdInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = profileService.getProfileByBearer(request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun getProfileByBearerTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(profileRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))

        val returned = profileService.getProfileByBearer(request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(profileRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(created.id)
                assertThat(it.email).isEqualTo(created.email)
            }
            .verifyComplete()
    }

    @Test
    fun getProfilesTest() {
        `when`(profileRepository.findAll()).thenReturn(Flux.just(created))

        val returned = profileService.getProfiles()

        verify(profileRepository, times(1)).findAll()
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(created.id)
                assertThat(it.email).isEqualTo(created.email)
            }
            .verifyComplete()
    }

    @Test
    fun deleteProfile_thenNotFound() {
        `when`(profileRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = profileService.deleteProfile(UUID.randomUUID())

        verify(profileRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun deleteProfileTest() {
        `when`(profileRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))
        `when`(profileRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = profileService.deleteProfile(UUID.randomUUID())

        verify(profileRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(created.id)
                assertThat(it.email).isEqualTo(created.email)
            }
            .verifyComplete()
    }
}