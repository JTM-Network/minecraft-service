package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.SuggestionDto
import com.jtm.plugin.core.domain.entity.Suggestion
import com.jtm.plugin.data.service.SuggestionService
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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(SuggestionController::class)
@AutoConfigureWebTestClient
class SuggestionControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var suggestionService: SuggestionService

    private val suggestion = Suggestion(originalPoster = "poster", pluginId = UUID.randomUUID(), comment = "A nice suggestion")
    private val suggestionTwo = Suggestion(originalPoster = "posterTwo", pluginId = UUID.randomUUID(), comment = "A quick suggestion")
    private val dto = SuggestionDto(comment = "test comment", pluginId = UUID.randomUUID())

    @Test
    fun postSuggestion() {
        `when`(suggestionService.addSuggestion(anyOrNull(), anyOrNull())).thenReturn(Mono.just(suggestion))

        testClient.post()
            .uri("/suggestion")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(suggestion.id.toString())
            .jsonPath("$.pluginId").isEqualTo(suggestion.pluginId.toString())
            .jsonPath("$.originalPoster").isEqualTo("poster")
            .jsonPath("$.comment").isEqualTo("A nice suggestion")

        verify(suggestionService, times(1)).addSuggestion(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun putSuggestion() {
        `when`(suggestionService.updateSuggestion(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(Mono.just(suggestion))

        testClient.put()
            .uri("/suggestion/${UUID.randomUUID()}")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(suggestion.id.toString())
            .jsonPath("$.pluginId").isEqualTo(suggestion.pluginId.toString())
            .jsonPath("$.originalPoster").isEqualTo("poster")
            .jsonPath("$.comment").isEqualTo("A nice suggestion")

        verify(suggestionService, times(1)).updateSuggestion(anyOrNull(), anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun getSuggestion() {
        `when`(suggestionService.getSuggestion(anyOrNull())).thenReturn(Mono.just(suggestion))

        testClient.get()
            .uri("/suggestion/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(suggestion.id.toString())
            .jsonPath("$.pluginId").isEqualTo(suggestion.pluginId.toString())
            .jsonPath("$.originalPoster").isEqualTo("poster")
            .jsonPath("$.comment").isEqualTo("A nice suggestion")

        verify(suggestionService, times(1)).getSuggestion(anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun getSuggestionsByAccount() {
        `when`(suggestionService.getSuggestionsByAccount(anyOrNull())).thenReturn(Flux.just(suggestion, suggestionTwo))

        testClient.get()
            .uri("/suggestion/account")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(suggestion.id.toString())
            .jsonPath("$[0].pluginId").isEqualTo(suggestion.pluginId.toString())
            .jsonPath("$[0].originalPoster").isEqualTo("poster")
            .jsonPath("$[0].comment").isEqualTo("A nice suggestion")
            .jsonPath("$[1].id").isEqualTo(suggestionTwo.id.toString())
            .jsonPath("$[1].pluginId").isEqualTo(suggestionTwo.pluginId.toString())
            .jsonPath("$[1].originalPoster").isEqualTo("posterTwo")
            .jsonPath("$[1].comment").isEqualTo("A quick suggestion")

        verify(suggestionService, times(1)).getSuggestionsByAccount(anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun getSuggestionsByAccountId() {
        `when`(suggestionService.getSuggestionsByAccountId(anyString())).thenReturn(Flux.just(suggestion, suggestionTwo))

        testClient.get()
            .uri("/suggestion/account/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(suggestion.id.toString())
            .jsonPath("$[0].pluginId").isEqualTo(suggestion.pluginId.toString())
            .jsonPath("$[0].originalPoster").isEqualTo("poster")
            .jsonPath("$[0].comment").isEqualTo("A nice suggestion")
            .jsonPath("$[1].id").isEqualTo(suggestionTwo.id.toString())
            .jsonPath("$[1].pluginId").isEqualTo(suggestionTwo.pluginId.toString())
            .jsonPath("$[1].originalPoster").isEqualTo("posterTwo")
            .jsonPath("$[1].comment").isEqualTo("A quick suggestion")

        verify(suggestionService, times(1)).getSuggestionsByAccountId(anyString())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun getSuggestionsByPluginId() {
        `when`(suggestionService.getSuggestionsByPluginId(anyOrNull())).thenReturn(Flux.just(suggestion, suggestionTwo))

        testClient.get()
            .uri("/suggestion/plugin/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(suggestion.id.toString())
            .jsonPath("$[0].pluginId").isEqualTo(suggestion.pluginId.toString())
            .jsonPath("$[0].originalPoster").isEqualTo("poster")
            .jsonPath("$[0].comment").isEqualTo("A nice suggestion")
            .jsonPath("$[1].id").isEqualTo(suggestionTwo.id.toString())
            .jsonPath("$[1].pluginId").isEqualTo(suggestionTwo.pluginId.toString())
            .jsonPath("$[1].originalPoster").isEqualTo("posterTwo")
            .jsonPath("$[1].comment").isEqualTo("A quick suggestion")

        verify(suggestionService, times(1)).getSuggestionsByPluginId(anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun getSuggestions() {
        `when`(suggestionService.getSuggestions()).thenReturn(Flux.just(suggestion, suggestionTwo))

        testClient.get()
            .uri("/suggestion/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].id").isEqualTo(suggestion.id.toString())
            .jsonPath("$[0].pluginId").isEqualTo(suggestion.pluginId.toString())
            .jsonPath("$[0].originalPoster").isEqualTo("poster")
            .jsonPath("$[0].comment").isEqualTo("A nice suggestion")
            .jsonPath("$[1].id").isEqualTo(suggestionTwo.id.toString())
            .jsonPath("$[1].pluginId").isEqualTo(suggestionTwo.pluginId.toString())
            .jsonPath("$[1].originalPoster").isEqualTo("posterTwo")
            .jsonPath("$[1].comment").isEqualTo("A quick suggestion")

        verify(suggestionService, times(1)).getSuggestions()
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun deleteSuggestion() {
        `when`(suggestionService.removeSuggestion(anyOrNull())).thenReturn(Mono.just(suggestion))

        testClient.delete()
            .uri("/suggestion/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(suggestion.id.toString())
            .jsonPath("$.pluginId").isEqualTo(suggestion.pluginId.toString())
            .jsonPath("$.originalPoster").isEqualTo("poster")
            .jsonPath("$.comment").isEqualTo("A nice suggestion")

        verify(suggestionService, times(1)).removeSuggestion(anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }
}