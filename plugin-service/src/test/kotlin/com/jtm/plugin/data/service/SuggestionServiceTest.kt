package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.SuggestionDto
import com.jtm.plugin.core.domain.entity.Suggestion
import com.jtm.plugin.core.domain.exception.profile.ClientIdNotFound
import com.jtm.plugin.core.domain.exception.profile.NotAllowedToPost
import com.jtm.plugin.core.domain.exception.suggestion.SuggestionNotFound
import com.jtm.plugin.core.usecase.repository.SuggestionRepository
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
    private val suggestionService = SuggestionService(suggestionRepository)
    private val suggestion = Suggestion(originalPoster = "poster", pluginId = UUID.randomUUID(), comment = "A nice suggestion")
    private val suggestionTwo = Suggestion(originalPoster = "posterTwo", pluginId = UUID.randomUUID(), comment = "A quick suggestion")
    private val mockSuggestion: Suggestion = mock()
    private val dto = SuggestionDto("test comment", UUID.randomUUID())

    private val req: ServerHttpRequest = mock()
    private val headers: HttpHeaders = mock()

    @Before
    fun setup() {
        `when`(req.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("CLIENT_ID")
    }

    @Test
    fun addSuggestion_thenClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = suggestionService.addSuggestion(req, dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun addSuggestion_thenNotAllowedToPost() {
        `when`(suggestionRepository.findByPluginIdAndOriginalPoster(anyOrNull(), anyString())).thenReturn(Flux.just(suggestion, suggestionTwo))

        val returned = suggestionService.addSuggestion(req, dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(suggestionRepository, times(1)).findByPluginIdAndOriginalPoster(anyOrNull(), anyString())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .expectError(NotAllowedToPost::class.java)
            .verify()
    }

    @Test
    fun addSuggestion() {
        `when`(mockSuggestion.posted).thenReturn(System.currentTimeMillis())
        `when`(mockSuggestion.canPost()).thenReturn(true)
        `when`(suggestionRepository.findByPluginIdAndOriginalPoster(anyOrNull(), anyString())).thenReturn(Flux.just(mockSuggestion))
        `when`(suggestionRepository.save(anyOrNull())).thenReturn(Mono.just(suggestionTwo))

        val returned = suggestionService.addSuggestion(req, dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(suggestionRepository, times(1)).findByPluginIdAndOriginalPoster(anyOrNull(), anyString())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(suggestionTwo.id)
                assertThat(it.pluginId).isEqualTo(suggestionTwo.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestionTwo.originalPoster)
                assertThat(it.comment).isEqualTo(suggestionTwo.comment)
            }
            .verifyComplete()
    }

    @Test
    fun updateSuggestion_thenClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = suggestionService.updateSuggestion(req, UUID.randomUUID(), dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun updateSuggestion_thenNotFound() {
        `when`(suggestionRepository.findByIdAndPluginIdAndOriginalPoster(anyOrNull(), anyOrNull(), anyString())).thenReturn(Mono.empty())

        val returned = suggestionService.updateSuggestion(req, UUID.randomUUID(), dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(suggestionRepository, times(1)).findByIdAndPluginIdAndOriginalPoster(anyOrNull(), anyOrNull(), anyString())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .expectError(SuggestionNotFound::class.java)
            .verify()
    }

    @Test
    fun updateSuggestion() {
        `when`(suggestionRepository.findByIdAndPluginIdAndOriginalPoster(anyOrNull(), anyOrNull(), anyString())).thenReturn(Mono.just(suggestion))
        `when`(suggestionRepository.save(anyOrNull())).thenReturn(Mono.just(suggestion))

        val returned = suggestionService.updateSuggestion(req, UUID.randomUUID(), dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(suggestionRepository, times(1)).findByIdAndPluginIdAndOriginalPoster(anyOrNull(), anyOrNull(), anyString())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(suggestion.id)
                assertThat(it.pluginId).isEqualTo(suggestion.pluginId)
                assertThat(it.originalPoster).isEqualTo("poster")
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
        `when`(suggestionRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(suggestion))

        val returned = suggestionService.getSuggestion(UUID.randomUUID())

        verify(suggestionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(suggestion.id)
                assertThat(it.pluginId).isEqualTo(suggestion.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestion.originalPoster)
                assertThat(it.comment).isEqualTo(suggestion.comment)
            }
            .verifyComplete()
    }

    @Test
    fun getSuggestionsByAccount_thenClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = suggestionService.getSuggestionsByAccount(req)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun getSuggestionsByAccount() {
        `when`(suggestionRepository.findByOriginalPoster(anyString())).thenReturn(Flux.just(suggestion, suggestionTwo))

        val returned = suggestionService.getSuggestionsByAccount(req)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(suggestionRepository, times(1)).findByOriginalPoster(anyString())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(suggestion.id)
                assertThat(it.pluginId).isEqualTo(suggestion.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestion.originalPoster)
                assertThat(it.comment).isEqualTo(suggestion.comment)
            }
            .assertNext {
                assertThat(it.id).isEqualTo(suggestionTwo.id)
                assertThat(it.pluginId).isEqualTo(suggestionTwo.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestionTwo.originalPoster)
                assertThat(it.comment).isEqualTo(suggestionTwo.comment)
            }
            .verifyComplete()
    }

    @Test
    fun getSuggestionsByAccountId() {
        `when`(suggestionRepository.findByOriginalPoster(anyString())).thenReturn(Flux.just(suggestion, suggestionTwo))

        val returned = suggestionService.getSuggestionsByAccountId("accountId")

        verify(suggestionRepository, times(1)).findByOriginalPoster(anyString())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(suggestion.id)
                assertThat(it.pluginId).isEqualTo(suggestion.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestion.originalPoster)
                assertThat(it.comment).isEqualTo(suggestion.comment)
            }
            .assertNext {
                assertThat(it.id).isEqualTo(suggestionTwo.id)
                assertThat(it.pluginId).isEqualTo(suggestionTwo.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestionTwo.originalPoster)
                assertThat(it.comment).isEqualTo(suggestionTwo.comment)
            }
            .verifyComplete()
    }

    @Test
    fun getSuggestionsByPluginId() {
        `when`(suggestionRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(suggestion, suggestionTwo))

        val returned = suggestionService.getSuggestionsByPluginId(UUID.randomUUID())

        verify(suggestionRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(suggestion.id)
                assertThat(it.pluginId).isEqualTo(suggestion.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestion.originalPoster)
                assertThat(it.comment).isEqualTo(suggestion.comment)
            }
            .assertNext {
                assertThat(it.id).isEqualTo(suggestionTwo.id)
                assertThat(it.pluginId).isEqualTo(suggestionTwo.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestionTwo.originalPoster)
                assertThat(it.comment).isEqualTo(suggestionTwo.comment)
            }
            .verifyComplete()
    }

    @Test
    fun getSuggestions() {
        `when`(suggestionRepository.findAll()).thenReturn(Flux.just(suggestion, suggestionTwo))

        val returned = suggestionService.getSuggestions()

        verify(suggestionRepository, times(1)).findAll()
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(suggestion.id)
                assertThat(it.pluginId).isEqualTo(suggestion.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestion.originalPoster)
                assertThat(it.comment).isEqualTo(suggestion.comment)
            }
            .assertNext {
                assertThat(it.id).isEqualTo(suggestionTwo.id)
                assertThat(it.pluginId).isEqualTo(suggestionTwo.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestionTwo.originalPoster)
                assertThat(it.comment).isEqualTo(suggestionTwo.comment)
            }
            .verifyComplete()
    }

    @Test
    fun removeSuggestion_thenNotFound() {
        `when`(suggestionRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = suggestionService.removeSuggestion(UUID.randomUUID())

        verify(suggestionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .expectError(SuggestionNotFound::class.java)
            .verify()
    }

    @Test
    fun removeSuggestion() {
        `when`(suggestionRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(suggestion))
        `when`(suggestionRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = suggestionService.removeSuggestion(UUID.randomUUID())

        verify(suggestionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(suggestionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(suggestion.id)
                assertThat(it.pluginId).isEqualTo(suggestion.pluginId)
                assertThat(it.originalPoster).isEqualTo(suggestion.originalPoster)
                assertThat(it.comment).isEqualTo(suggestion.comment)
            }
            .verifyComplete()
    }
}