package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.WikiTopicDto
import com.jtm.minecraft.core.domain.entity.plugin.PluginWiki
import com.jtm.minecraft.core.domain.exceptions.plugin.wiki.PluginWikiNotFound
import com.jtm.minecraft.core.domain.exceptions.plugin.wiki.WikiTopicFound
import com.jtm.minecraft.core.domain.exceptions.plugin.wiki.WikiTopicNotFound
import com.jtm.minecraft.core.domain.model.WikiTopic
import com.jtm.minecraft.core.usecase.repository.plugin.PluginWikiRepository
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

    private val wikiRepository: PluginWikiRepository = mock()
    private val wikiService = WikiService(wikiRepository)
    private val wiki: PluginWiki = mock()
    private val topic: WikiTopic = WikiTopic("test", "title", "html")
    private val dto = WikiTopicDto("test", "title", "html")

    @Test
    fun insertTopic_thenTopicFound() {
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
        `when`(wiki.addWiki(anyOrNull())).thenReturn(topic)
        `when`(wikiRepository.save(anyOrNull())).thenReturn(Mono.just(wiki))

        val returned = wikiService.insertTopic(UUID.randomUUID(), dto)

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.name).isEqualTo("test")
                    assertThat(it.title).isEqualTo("title")
                    assertThat(it.html).isEqualTo("html")
                }
                .verifyComplete()
    }

    @Test
    fun updateTopic_thenPluginWikiNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = wikiService.updateTopic(UUID.randomUUID(), dto)

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
                .expectError(PluginWikiNotFound::class.java)
                .verify()
    }

    @Test
    fun updateTopic_thenTopicNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.exists(anyString())).thenReturn(false)

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
        `when`(wiki.updateWiki(anyOrNull())).thenReturn(topic)
        `when`(wikiRepository.save(anyOrNull())).thenReturn(Mono.just(wiki))

        val returned = wikiService.updateTopic(UUID.randomUUID(), dto)

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.name).isEqualTo("test")
                    assertThat(it.title).isEqualTo("title")
                    assertThat(it.html).isEqualTo("html")
                }
                .verifyComplete()
    }

    @Test
    fun getTopic_thenPluginWikiNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = wikiService.getTopic(UUID.randomUUID(), "test")

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
                .expectError(PluginWikiNotFound::class.java)
                .verify()
    }

    @Test
    fun getTopic_thenTopicNotFound() {
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
                    assertThat(it.name).isEqualTo("test")
                    assertThat(it.title).isEqualTo("title")
                    assertThat(it.html).isEqualTo("html")
                }
                .verifyComplete()
    }

    @Test
    fun getTopics_thenPluginWikiNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = wikiService.getTopics(UUID.randomUUID())

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
                .expectError(PluginWikiNotFound::class.java)
                .verify()
    }

    @Test
    fun getTopics() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.topics).thenReturn(mutableMapOf(topic.name to topic))

        val returned = wikiService.getTopics(UUID.randomUUID())

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.name).isEqualTo("test")
                    assertThat(it.title).isEqualTo("title")
                    assertThat(it.html).isEqualTo("html")
                }
                .verifyComplete()
    }

    @Test
    fun removeTopic_thenPluginWikiNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = wikiService.removeTopic(UUID.randomUUID(), "test")

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
                .expectError(PluginWikiNotFound::class.java)
                .verify()
    }

    @Test
    fun removeTopic_thenTopicNotFound() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.exists(anyString())).thenReturn(false)

        val returned = wikiService.removeTopic(UUID.randomUUID(), "test")

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
                .expectError(WikiTopicNotFound::class.java)
                .verify()
    }

    @Test
    fun removeTopic() {
        `when`(wikiRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(wiki))
        `when`(wiki.exists(anyString())).thenReturn(true)
        `when`(wiki.getWiki(anyString())).thenReturn(topic)
        `when`(wiki.removeWiki(anyString())).thenReturn(wiki)
        `when`(wikiRepository.save(anyOrNull())).thenReturn(Mono.just(wiki))

        val returned = wikiService.removeTopic(UUID.randomUUID(), "test")

        verify(wikiRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(wikiRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.name).isEqualTo("test")
                    assertThat(it.title).isEqualTo("title")
                    assertThat(it.html).isEqualTo("html")
                }
                .verifyComplete()
    }
}