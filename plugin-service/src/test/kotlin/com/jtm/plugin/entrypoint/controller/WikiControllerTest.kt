package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.WikiTopicDto
import com.jtm.plugin.core.domain.entity.Wiki
import com.jtm.plugin.core.domain.model.WikiTopic
import com.jtm.plugin.data.service.WikiService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
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
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(WikiController::class)
@AutoConfigureWebTestClient
class WikiControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var wikiService: WikiService

    private val topic = WikiTopic("test", "title", "HTML")
    private val dto = WikiTopicDto("test", "title", "HTML")

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
            .jsonPath("$.html").isEqualTo("HTML")

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
            .jsonPath("$.html").isEqualTo("HTML")

        verify(wikiService, times(1)).updateTopic(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(wikiService)
    }

    @Test
    fun getTopic() {
        `when`(wikiService.getTopic(anyOrNull(), anyString())).thenReturn(Mono.just(topic))

        testClient.get()
            .uri("/wiki/${UUID.randomUUID()}/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.title").isEqualTo("title")
            .jsonPath("$.html").isEqualTo("HTML")

        verify(wikiService, times(1)).getTopic(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(wikiService)
    }

    @Test
    fun getTopics() {
        `when`(wikiService.getTopics(anyOrNull())).thenReturn(Flux.just(topic, WikiTopic("test #2", "title #2", "HTML #2")))

        testClient.get()
            .uri("/wiki/${UUID.randomUUID()}/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].name").isEqualTo("test")
            .jsonPath("$[0].title").isEqualTo("title")
            .jsonPath("$[0].html").isEqualTo("HTML")
            .jsonPath("$[1].name").isEqualTo("test #2")
            .jsonPath("$[1].title").isEqualTo("title #2")
            .jsonPath("$[1].html").isEqualTo("HTML #2")

        verify(wikiService, times(1)).getTopics(anyOrNull())
        verifyNoMoreInteractions(wikiService)
    }

    @Test
    fun deleteTopic() {
        `when`(wikiService.removeTopic(anyOrNull(), anyString())).thenReturn(Mono.just(topic))

        testClient.delete()
            .uri("/wiki/${UUID.randomUUID()}/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.title").isEqualTo("title")
            .jsonPath("$.html").isEqualTo("HTML")

        verify(wikiService, times(1)).removeTopic(anyOrNull(), anyString())
        verifyNoMoreInteractions(wikiService)
    }
}