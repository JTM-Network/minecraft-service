package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.dto.WikiTopicDto
import com.jtm.minecraft.core.domain.entity.plugin.PluginWiki
import com.jtm.minecraft.core.domain.model.WikiTopic
import com.jtm.minecraft.data.service.plugin.WikiService
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
@WebFluxTest(WikiController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
class WikiControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var wikiService: WikiService

    private val topic = WikiTopic("test", "title", "html")
    private val dto = WikiTopicDto("test", "title", "html")

    @Test
    fun postTopic() {
        `when`(wikiService.insertTopic(anyOrNull(), anyOrNull())).thenReturn(Mono.just(topic))

        testClient.post()
                .uri("/wiki/${UUID.randomUUID()}")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.name").isEqualTo("test")
                .jsonPath("$.title").isEqualTo("title")
                .jsonPath("$.html").isEqualTo("html")

        verify(wikiService, times(1)).insertTopic(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(wikiService)
    }

    @Test
    fun putTopic() {
        `when`(wikiService.updateTopic(anyOrNull(), anyOrNull())).thenReturn(Mono.just(topic))

        testClient.put()
                .uri("/wiki/${UUID.randomUUID()}")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.name").isEqualTo("test")
                .jsonPath("$.title").isEqualTo("title")
                .jsonPath("$.html").isEqualTo("html")

        verify(wikiService, times(1)).updateTopic(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(wikiService)
    }

    @Test
    fun getTopic() {
        `when`(wikiService.getTopic(anyOrNull(), anyOrNull())).thenReturn(Mono.just(topic))

        testClient.get()
                .uri("/wiki/${UUID.randomUUID()}/test")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.name").isEqualTo("test")
                .jsonPath("$.title").isEqualTo("title")
                .jsonPath("$.html").isEqualTo("html")

        verify(wikiService, times(1)).getTopic(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(wikiService)
    }

    @Test
    fun getTopics() {
        `when`(wikiService.getTopics(anyOrNull())).thenReturn(Flux.just(topic))

        testClient.get()
                .uri("/wiki/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("test")
                .jsonPath("$[0].title").isEqualTo("title")
                .jsonPath("$[0].html").isEqualTo("html")

        verify(wikiService, times(1)).getTopics(anyOrNull())
        verifyNoMoreInteractions(wikiService)
    }

    @Test
    fun removeTopic() {
        `when`(wikiService.removeTopic(anyOrNull(), anyOrNull())).thenReturn(Mono.just(topic))

        testClient.delete()
                .uri("/wiki/${UUID.randomUUID()}/test")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.name").isEqualTo("test")
                .jsonPath("$.title").isEqualTo("title")
                .jsonPath("$.html").isEqualTo("html")

        verify(wikiService, times(1)).removeTopic(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(wikiService)
    }
}