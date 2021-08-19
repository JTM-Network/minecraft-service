package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.entity.BlacklistToken
import com.jtm.minecraft.core.domain.exceptions.token.BlacklistTokenNotFound
import com.jtm.minecraft.core.usecase.repository.BlacklistTokenRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
class BlacklistTokenServiceTest {

    private val tokenRepository: BlacklistTokenRepository = mock()
    private val tokenService = BlacklistTokenService(tokenRepository)
    private val created = BlacklistToken("token")

    @Test
    fun insertTokenTest() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.empty())
        `when`(tokenRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = tokenService.insertToken("test")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.token).isEqualTo("token")
                assertThat(it.timestamp).isLessThan(System.currentTimeMillis())
            }
            .verifyComplete()
    }

    @Test
    fun isBlacklistedTest() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.just(created))

        val returned = tokenService.isBlacklisted("test")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it).isTrue() }
            .verifyComplete()
    }

    @Test
    fun isBlacklisted_thenFalse() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = tokenService.isBlacklisted("test")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it).isFalse() }
            .verifyComplete()
    }

    @Test
    fun getTokensTest() {
        `when`(tokenRepository.findAll()).thenReturn(Flux.just(created))

        val returned = tokenService.getTokens()

        verify(tokenRepository, times(1)).findAll()
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.token).isEqualTo(created.token)
                assertThat(it.timestamp).isEqualTo(created.timestamp)
            }
            .verifyComplete()
    }

    @Test
    fun deleteToken_thenNotFound() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = tokenService.deleteToken("test")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .expectError(BlacklistTokenNotFound::class.java)
            .verify()
    }

    @Test
    fun deleteTokenTest() {
        `when`(tokenRepository.findById(anyString())).thenReturn(Mono.just(created))
        `when`(tokenRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = tokenService.deleteToken("test")

        verify(tokenRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(tokenRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.token).isEqualTo(created.token)
                assertThat(it.timestamp).isEqualTo(created.timestamp)
            }
            .verifyComplete()
    }
}