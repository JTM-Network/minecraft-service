package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.data.service.ProfileService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(ProfileController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
class ProfileControllerTest {

    @Autowired lateinit var testClient: WebTestClient

    @MockBean lateinit var profileService: ProfileService
    private val created = Profile(email = "test@gmail.com")

    @Test
    fun getProfileTest() {
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/profile/id/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(created.id.toString())
            .jsonPath("$.email").isEqualTo(created.email)

        verify(profileService, times(1)).getProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun getProfileByBearerTest() {
        `when`(profileService.getProfileByBearer(anyOrNull())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/profile/me")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(created.id.toString())
            .jsonPath("$.email").isEqualTo(created.email)

        verify(profileService, times(1)).getProfileByBearer(anyOrNull())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun getProfilesTest() {
        `when`(profileService.getProfiles()).thenReturn(Flux.just(created))

        testClient.get()
            .uri("/profile/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(created.id.toString())
            .jsonPath("$[0].email").isEqualTo(created.email)

        verify(profileService, times(1)).getProfiles()
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun deleteProfileTest() {
        `when`(profileService.deleteProfile(anyOrNull())).thenReturn(Mono.just(created))

        testClient.delete()
            .uri("/profile/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(created.id.toString())
            .jsonPath("$.email").isEqualTo(created.email)

        verify(profileService, times(1)).deleteProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)
    }
}