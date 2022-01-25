package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.WikiTopicDto
import com.jtm.minecraft.core.domain.entity.plugin.PluginWiki
import com.jtm.minecraft.core.domain.exceptions.plugin.wiki.PluginWikiNotFound
import com.jtm.minecraft.core.domain.exceptions.plugin.wiki.WikiTopicFound
import com.jtm.minecraft.core.domain.exceptions.plugin.wiki.WikiTopicNotFound
import com.jtm.minecraft.core.domain.model.WikiTopic
import com.jtm.minecraft.core.usecase.repository.plugin.PluginWikiRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class WikiService @Autowired constructor(private val wikiRepository: PluginWikiRepository) {

    fun insertTopic(id: UUID, dto: WikiTopicDto): Mono<WikiTopic> {
        return wikiRepository.findById(id)
                .switchIfEmpty(Mono.defer { wikiRepository.save(PluginWiki(id = id)) })
                .flatMap {
                    if (it.exists(dto.name)) return@flatMap Mono.error(WikiTopicFound())
                    val topic = it.addWiki(dto)
                    wikiRepository.save(it).thenReturn(topic)
                }
    }

    fun updateTopic(id: UUID, dto: WikiTopicDto): Mono<WikiTopic> {
        return wikiRepository.findById(id)
                .switchIfEmpty(Mono.defer { Mono.error(PluginWikiNotFound()) })
                .flatMap {
                    if (!it.exists(dto.name)) return@flatMap Mono.error(WikiTopicNotFound())
                    val topic = it.updateWiki(dto) ?: return@flatMap Mono.error(WikiTopicNotFound())
                    wikiRepository.save(it).thenReturn(topic)
                }
    }

    fun getTopic(id: UUID, name: String): Mono<WikiTopic> {
        return wikiRepository.findById(id)
                .switchIfEmpty(Mono.defer { Mono.error(PluginWikiNotFound()) })
                .flatMap {
                    if (!it.exists(name)) return@flatMap Mono.error(WikiTopicNotFound())
                    val topic = it.getWiki(name) ?: return@flatMap Mono.error(WikiTopicNotFound())
                    return@flatMap Mono.just(topic)
                }
    }

    fun getTopics(id: UUID): Flux<WikiTopic> {
        return wikiRepository.findById(id)
                .switchIfEmpty(Mono.defer { Mono.error(PluginWikiNotFound()) })
                .flatMapMany { Flux.fromIterable(it.topics.values) }

    }

    fun removeTopic(id: UUID, name: String): Mono<WikiTopic> {
        return wikiRepository.findById(id)
                .switchIfEmpty(Mono.defer { Mono.error(PluginWikiNotFound()) })
                .flatMap {
                    if (!it.exists(name)) return@flatMap Mono.error(WikiTopicNotFound())
                    val topic = it.getWiki(name) ?: return@flatMap Mono.error(WikiTopicNotFound())
                    wikiRepository.save(it.removeWiki(name)).thenReturn(topic)
                }
    }
}