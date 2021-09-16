package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.SuggestionDto
import com.jtm.minecraft.core.domain.entity.plugin.Suggestion
import com.jtm.minecraft.core.domain.exceptions.plugin.suggestion.SuggestionFound
import com.jtm.minecraft.core.domain.exceptions.plugin.suggestion.SuggestionNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.repository.plugin.SuggestionRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
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
class SuggestionServiceTest {

    private val suggestionRepository: SuggestionRepository = mock()
    private val tokenProvider: AccountTokenProvider = mock()
    private val suggestionService = SuggestionService(suggestionRepository, tokenProvider)

    private val created = Suggestion(accountId = UUID.randomUUID(), pluginId = UUID.randomUUID(), comment = "test")
    private val dto = SuggestionDto(pluginId = UUID.randomUUID(), comment = "test comment")

    private val request: ServerHttpRequest = mock()
    private val account = UUID.randomUUID()

    @Before
    fun setup() {
        val headers: HttpHeaders = mock()

        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("Bearer test")
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(account)
    }

    @Test
    fun addSuggestion_thenInvalidToken() {
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = suggestionService.addSuggestion(request, dto)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
                .expectError(InvalidJwtToken::class.java)
                .verify()
    }

    @Test
    fun addSuggestion_thenFound() {
        `when`(suggestionRepository.findByAccountIdAndPluginId(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        val returned = suggestionService.addSuggestion(request, dto)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(suggestionRepository, times(1)).findByAccountIdAndPluginId(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .expectError(SuggestionFound::class.java)
                .verify()
    }

    @Test
    fun addSuggestion() {
        `when`(suggestionRepository.findByAccountIdAndPluginId(anyOrNull(), anyOrNull())).thenReturn(Mono.empty())
        `when`(suggestionRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = suggestionService.addSuggestion(request, dto)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(suggestionRepository, times(1)).findByAccountIdAndPluginId(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }

    @Test
    fun updateSuggestionComment_thenInvalidToken() {
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = suggestionService.updateSuggestionComment(request, dto)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
                .expectError(InvalidJwtToken::class.java)
                .verify()
    }

    @Test
    fun updateSuggestionComment_thenNotFound() {
        `when`(suggestionRepository.findByAccountIdAndPluginId(anyOrNull(), anyOrNull())).thenReturn(Mono.empty())

        val returned = suggestionService.updateSuggestionComment(request, dto)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(suggestionRepository, times(1)).findByAccountIdAndPluginId(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .expectError(SuggestionNotFound::class.java)
                .verify()
    }

    @Test
    fun updateSuggestionComment() {
        `when`(suggestionRepository.findByAccountIdAndPluginId(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))
        `when`(suggestionRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = suggestionService.updateSuggestionComment(request, dto)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(suggestionRepository, times(1)).findByAccountIdAndPluginId(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test comment")
                }
                .verifyComplete()
    }

    @Test
    fun getSuggestion_thenNotFound() {
        `when`(suggestionRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = suggestionService.getSuggestion(UUID.randomUUID())

        verify(suggestionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .expectError(SuggestionNotFound::class.java)
                .verify()
    }

    @Test
    fun getSuggestion() {
        `when`(suggestionRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))

        val returned = suggestionService.getSuggestion(UUID.randomUUID())

        verify(suggestionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }

    @Test
    fun getSuggestionsByPlugin() {
        `when`(suggestionRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(created))

        val returned = suggestionService.getSuggestionsByPlugin(UUID.randomUUID())

        verify(suggestionRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }

    @Test
    fun getSuggestionsByAccount() {
        `when`(suggestionRepository.findByAccountId(anyOrNull())).thenReturn(Flux.just(created))

        val returned = suggestionService.getSuggestionsByAccount(UUID.randomUUID())

        verify(suggestionRepository, times(1)).findByAccountId(anyOrNull())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }

    @Test
    fun getSuggestions() {
        `when`(suggestionRepository.findAll()).thenReturn(Flux.just(created))

        val returned = suggestionService.getSuggestions()

        verify(suggestionRepository, times(1)).findAll()
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }

    @Test
    fun deleteSuggestion_thenNotFound() {
        `when`(suggestionRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = suggestionService.deleteSuggestion(UUID.randomUUID())

        verify(suggestionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .expectError(SuggestionNotFound::class.java)
                .verify()
    }

    @Test
    fun deleteSuggestion() {
        `when`(suggestionRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))
        `when`(suggestionRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = suggestionService.deleteSuggestion(UUID.randomUUID())

        verify(suggestionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }
}