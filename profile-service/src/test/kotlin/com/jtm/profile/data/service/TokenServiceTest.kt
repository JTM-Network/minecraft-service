package com.jtm.profile.data.service

import com.jtm.profile.core.domain.entity.Profile
import com.jtm.profile.core.domain.entity.Token
import com.jtm.profile.core.domain.exceptions.ClientIdNotFound
import com.jtm.profile.core.domain.exceptions.ProfileNotFound
import com.jtm.profile.core.domain.exceptions.token.TokenNotFound
import com.jtm.profile.core.usecase.provider.TokenProvider
import com.jtm.profile.core.usecase.repository.ProfileRepository
import com.jtm.profile.core.usecase.repository.TokenRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class TokenServiceTest {

    private val tokenRepository: TokenRepository = mock()
    private val profileRepository: ProfileRepository = mock()
    private val tokenProvider: TokenProvider = mock()
    private val tokenService = TokenService(tokenRepository, profileRepository, tokenProvider)
    private val profile: Profile = mock()
    private val token = Token(token = "token", clientId = "clientId")
    private val tokenTwo = Token(token = "token2", clientId = "clientId2")

    private val request: ServerHttpRequest = mock()
    private val headers: HttpHeaders = mock()

    @Before
    fun setup() {
        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("clientId")
    }

    @Test
    fun generateToken_thenClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = tokenService.generateToken(request)

        verify(request, times(1)).headers
        verifyNoMoreInteractions(request)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun generateToken_thenProfileNotFound() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = tokenService.generateToken(request)

        verify(request, times(1)).headers
        verifyNoMoreInteractions(request)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .expectError(ProfileNotFound::class.java)
            .verify()
    }

    @Test
    fun generateToken() {
        `when`(profileRepository.findById(anyString())).thenReturn(Mono.just(profile))
        `when`(tokenRepository.save(anyOrNull())).thenReturn(Mono.just(token))
        `when`(tokenProvider.createToken(anyString())).thenReturn("token")

        val returned = tokenService.generateToken(request)

        verify(request, times(1)).headers
        verifyNoMoreInteractions(request)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(profileRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(profileRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.token).isEqualTo("token") }
            .verifyComplete()
    }

    @Test
    fun getTokenById_thenNotFound() {
        `when`(tokenRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = tokenService.getTokenById(UUID.randomUUID())

        verify(tokenRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(TokenNotFound::class.java)
            .verify()
    }

    @Test
    fun getTokenById() {
        `when`(tokenRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(token))

        val returned = tokenService.getTokenById(UUID.randomUUID())

        verify(tokenRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(token.id)
                assertThat(it.token).isEqualTo("token")
                assertThat(it.clientId).isEqualTo("clientId")
            }
            .verifyComplete()
    }

    @Test
    fun getToken_thenNotFound() {
        `when`(tokenRepository.findByToken(anyString())).thenReturn(Mono.empty())

        val returned = tokenService.getToken("test")

        verify(tokenRepository, times(1)).findByToken(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(TokenNotFound::class.java)
            .verify()
    }

    @Test
    fun getToken() {
        `when`(tokenRepository.findByToken(anyString())).thenReturn(Mono.just(token))

        val returned = tokenService.getToken("test")

        verify(tokenRepository, times(1)).findByToken(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(token.id)
                assertThat(it.token).isEqualTo("token")
                assertThat(it.clientId).isEqualTo("clientId")
            }
            .verifyComplete()
    }

    @Test
    fun getTokens() {
        `when`(tokenRepository.findAll()).thenReturn(Flux.just(token, tokenTwo))

        val returned = tokenService.getTokens()

        verify(tokenRepository, times(1)).findAll()
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(token.id)
                assertThat(it.token).isEqualTo("token")
                assertThat(it.clientId).isEqualTo("clientId")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(tokenTwo.id)
                assertThat(it.token).isEqualTo("token2")
                assertThat(it.clientId).isEqualTo("clientId2")
            }
            .verifyComplete()
    }

    @Test
    fun getTokensByAccount_thenClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = tokenService.getTokensByAccount(request)

        verify(request, times(1)).headers
        verifyNoMoreInteractions(request)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun getTokensByAccount() {
        `when`(tokenRepository.findByClientId(anyString())).thenReturn(Flux.just(token, tokenTwo))

        val returned = tokenService.getTokensByAccount(request)

        verify(tokenRepository, times(1)).findByClientId(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(token.id)
                assertThat(it.token).isEqualTo("token")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(tokenTwo.id)
                assertThat(it.token).isEqualTo("token2")
                assertThat(it.clientId).isEqualTo("clientId2")
            }
            .verifyComplete()
    }

    @Test
    fun getTokensByAccountId() {
        `when`(tokenRepository.findByClientId(anyString())).thenReturn(Flux.just(token, tokenTwo))

        val returned = tokenService.getTokensByAccountId("test")

        verify(tokenRepository, times(1)).findByClientId(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(token.id)
                assertThat(it.token).isEqualTo("token")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(tokenTwo.id)
                assertThat(it.token).isEqualTo("token2")
                assertThat(it.clientId).isEqualTo("clientId2")
            }
            .verifyComplete()
    }

    @Test
    fun removeToken_thenNotFound() {
        `when`(tokenRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = tokenService.removeToken(UUID.randomUUID())

        verify(tokenRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(TokenNotFound::class.java)
            .verify()
    }

    @Test
    fun removeToken() {
        `when`(tokenRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(token))
        `when`(tokenRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = tokenService.removeToken(UUID.randomUUID())

        verify(tokenRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(token.id)
                assertThat(it.token).isEqualTo("token")
            }
            .verifyComplete()
    }
}