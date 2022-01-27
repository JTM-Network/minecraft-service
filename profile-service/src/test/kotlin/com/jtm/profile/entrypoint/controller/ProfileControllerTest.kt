package com.jtm.profile.entrypoint.controller

import com.jtm.profile.core.domain.entity.Profile
import com.jtm.profile.data.service.ProfileService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@WebFluxTest(ProfileController::class)
@AutoConfigureWebTestClient
class ProfileControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var profileService: ProfileService

    private val profile = Profile("user_id")

    @Test
    fun getProfile() {
        `when`(profileService.getProfile(anyOrNull())).thenReturn(Mono.just(profile))

        testClient.get()
            .uri("/me")
            .header("CLIENT_ID", "user_id")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("user_id")

        verify(profileService, times(1)).getProfile(anyOrNull())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun banProfile() {
        `when`(profileService.banProfile(anyString())).thenReturn(Mono.just(profile))

        testClient.delete()
            .uri("/ban/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("user_id")

        verify(profileService, times(1)).banProfile(anyString())
        verifyNoMoreInteractions(profileService)
    }

    @Test
    fun unbanProfile() {
        `when`(profileService.unbanProfile(anyString())).thenReturn(Mono.just(profile))

        testClient.put()
            .uri("/unban/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("user_id")

        verify(profileService, times(1)).unbanProfile(anyString())
        verifyNoMoreInteractions(profileService)
    }
}