package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.WikiTopicDto
import com.jtm.plugin.core.domain.entity.Wiki
import com.jtm.plugin.core.domain.exception.wiki.WikiNotFound
import com.jtm.plugin.core.domain.exception.wiki.WikiTopicFound
import com.jtm.plugin.core.domain.exception.wiki.WikiTopicNotFound
import com.jtm.plugin.core.domain.model.WikiTopic
import com.jtm.plugin.core.usecase.repository.WikiRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class WikiServiceTest {

    private val wikiRepository: WikiRepository = mock()
    private val wikiService = WikiService(wikiRepository)
    private val wiki: Wiki = mock()
    private val topic = WikiTopic(name = "Test", title = "Test title", html = "HTML")
    private val dto = WikiTopicDto(name = "Test", title = "Title")

    @Test
    fun insertTopic_thenWikiTopicFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.exists(anyString())).thenReturn(true)

        val returned = wikiService.insertTopic(UUID.randomUUID(), dto)

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .expectError(WikiTopicFound::class.java)
            .verify()
    }

    @Test
    fun insertTopic() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.exists(anyString())).thenReturn(false)
        `when`(wiki.addWiki(dto)).thenReturn(topic)
        `when`(wikiRepository.save(anyOrNull())).thenReturn(Mono.just(wiki))

        val returned = wikiService.insertTopic(UUID.randomUUID(), dto)

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.title).isEqualTo("Test title")
                assertThat(it.html).isEqualTo("HTML")
            }
            .verifyComplete()
    }

    @Test
    fun updateTopic_thenWikiNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = wikiService.updateTopic(UUID.randomUUID(), dto)

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .expectError(WikiNotFound::class.java)
            .verify()
    }

    @Test
    fun updateTopic_thenWikiTopicNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.updateWiki(anyOrNull())).thenReturn(null)

        val returned = wikiService.updateTopic(UUID.randomUUID(), dto)

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .expectError(WikiTopicNotFound::class.java)
            .verify()
    }

    @Test
    fun updateTopic() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.exists(anyString())).thenReturn(true)
        `when`(wiki.updateWiki(dto)).thenReturn(topic)
        `when`(wikiRepository.save(anyOrNull())).thenReturn(Mono.just(wiki))

        val returned = wikiService.updateTopic(UUID.randomUUID(), dto)

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.title).isEqualTo("Test title")
                assertThat(it.html).isEqualTo("HTML")
            }
            .verifyComplete()
    }

    @Test
    fun getTopic_thenWikiNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = wikiService.getTopic(UUID.randomUUID(), "test")

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .expectError(WikiNotFound::class.java)
            .verify()
    }

    @Test
    fun getTopic_thenWikiTopicNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.exists(anyString())).thenReturn(false)

        val returned = wikiService.getTopic(UUID.randomUUID(), "test")

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .expectError(WikiTopicNotFound::class.java)
            .verify()
    }

    @Test
    fun getTopic() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.exists(anyString())).thenReturn(true)
        `when`(wiki.getWiki(anyString())).thenReturn(topic)

        val returned = wikiService.getTopic(UUID.randomUUID(), "test")

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.title).isEqualTo("Test title")
                assertThat(it.html).isEqualTo("HTML")
            }
            .verifyComplete()
    }

    @Test
    fun getTopics_thenWikiNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = wikiService.getTopics(UUID.randomUUID())

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .expectError(WikiNotFound::class.java)
            .verify()
    }

    @Test
    fun getTopics() {
        val topicTwo = WikiTopic("Test #2", "Test Title #2", "HTML")

        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.topics).thenReturn(mutableMapOf(topic.name to topic, topicTwo.name to topicTwo))

        val returned = wikiService.getTopics(UUID.randomUUID())

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.title).isEqualTo("Test title")
                assertThat(it.html).isEqualTo("HTML")
            }
            .assertNext {
                assertThat(it.name).isEqualTo("Test #2")
                assertThat(it.title).isEqualTo("Test Title #2")
                assertThat(it.html).isEqualTo("HTML")
            }
            .verifyComplete()
    }

    @Test
    fun removeTopic_thenWikiNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = wikiService.removeTopic(UUID.randomUUID(), "Test")

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .expectError(WikiNotFound::class.java)
            .verify()
    }

    @Test
    fun removeTopic_thenWikiTopicNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.getWiki(anyString())).thenReturn(null)

        val returned = wikiService.removeTopic(UUID.randomUUID(), "Test")

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .expectError(WikiTopicNotFound::class.java)
            .verify()
    }

    @Test
    fun removeTopic() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.getWiki(anyString())).thenReturn(topic)
        `when`(wiki.removeWiki(anyString())).thenReturn(wiki)
        `when`(wikiRepository.save(anyOrNull())).thenReturn(Mono.just(wiki))

        val returned = wikiService.removeTopic(UUID.randomUUID(), "test")

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.title).isEqualTo("Test title")
                assertThat(it.html).isEqualTo("HTML")
            }
            .verifyComplete()
    }
}