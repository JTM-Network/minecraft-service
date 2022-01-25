package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.dto.SuggestionDto
import com.jtm.minecraft.core.domain.entity.plugin.Suggestion
import com.jtm.minecraft.data.service.plugin.SuggestionService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(SuggestionController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class SuggestionControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var suggestionService: SuggestionService

    private val created = Suggestion(accountId = UUID.randomUUID(), pluginId = UUID.randomUUID(), comment = "test")
    private val dto = SuggestionDto(pluginId = UUID.randomUUID(), "test comment")

    @Test
    fun postSuggestion() {
        `when`(suggestionService.addSuggestion(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.post()
                .uri("/suggestion")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$.comment").isEqualTo("test")

        verify(suggestionService, times(1)).addSuggestion(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun putSuggestionComment() {
        `when`(suggestionService.updateSuggestionComment(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
                .uri("/suggestion")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$.comment").isEqualTo("test")

        verify(suggestionService, times(1)).updateSuggestionComment(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun getSuggestion() {
        `when`(suggestionService.getSuggestion(anyOrNull())).thenReturn(Mono.just(created))

        testClient.get()
                .uri("/suggestion/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$.comment").isEqualTo("test")

        verify(suggestionService, times(1)).getSuggestion(anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun getSuggestionsByPlugin() {
        `when`(suggestionService.getSuggestionsByPlugin(anyOrNull())).thenReturn(Flux.just(created))

        testClient.get()
                .uri("/suggestion/plugin/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$[0].accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$[0].comment").isEqualTo("test")

        verify(suggestionService, times(1)).getSuggestionsByPlugin(anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun getSuggestionsByAccount() {
        `when`(suggestionService.getSuggestionsByAccount(anyOrNull())).thenReturn(Flux.just(created))

        testClient.get()
                .uri("/suggestion/account/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$[0].accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$[0].comment").isEqualTo("test")

        verify(suggestionService, times(1)).getSuggestionsByAccount(anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun getSuggestions() {
        `when`(suggestionService.getSuggestions()).thenReturn(Flux.just(created))

        testClient.get()
                .uri("/suggestion/all")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$[0].accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$[0].comment").isEqualTo("test")

        verify(suggestionService, times(1)).getSuggestions()
        verifyNoMoreInteractions(suggestionService)
    }

    @Test
    fun deleteSuggestion() {
        `when`(suggestionService.deleteSuggestion(anyOrNull())).thenReturn(Mono.just(created))

        testClient.delete()
                .uri("/suggestion/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$.comment").isEqualTo("test")

        verify(suggestionService, times(1)).deleteSuggestion(anyOrNull())
        verifyNoMoreInteractions(suggestionService)
    }
}