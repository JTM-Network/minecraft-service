package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.WikiTopicDto
import com.jtm.plugin.core.domain.entity.Wiki
import com.jtm.plugin.core.domain.exception.wiki.WikiNotFound
import com.jtm.plugin.core.domain.exception.wiki.WikiTopicFound
import com.jtm.plugin.core.domain.exception.wiki.WikiTopicNotFound
import com.jtm.plugin.core.domain.model.WikiTopic
import com.jtm.plugin.core.usecase.repository.WikiRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class WikiService @Autowired constructor(private val wikiRepository: WikiRepository) {

    /**
     * This will insert a wiki topic under the identifier of the plugin, if a wiki is not found,
     * it will create one using the identifier of the plugin.
     *
     * @param id        the plugin identifier
     * @param dto       the data transfer object of the wiki topic information
     * @see             WikiTopic
     * @throws WikiTopicFound if the name value is found under a different topic
     */
    fun insertTopic(id: UUID, dto: WikiTopicDto): Mono<WikiTopic> {
        return wikiRepository.findById(id)
            .switchIfEmpty(Mono.defer { wikiRepository.save(Wiki(id)) })
            .flatMap {
                if (it.exists(dto.name)) return@flatMap Mono.error(WikiTopicFound())
                val topic = it.addWiki(dto)
                wikiRepository.save(it).thenReturn(topic)
            }
    }

    /**
     * This will update the wiki topic found by the name value in the data transfer object.
     *
     * @param id        the plugin identifier
     * @param dto       the data transfer object
     * @see             WikiTopic
     * @throws WikiNotFound if the wiki is not found using the identifier
     * @throws WikiTopicNotFound if the wiki topic is not found using the name value in data transfer object
     */
    fun updateTopic(id: UUID, dto: WikiTopicDto): Mono<WikiTopic> {
        return wikiRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(WikiNotFound()) })
            .flatMap {
                val topic = it.updateWiki(dto) ?: return@flatMap Mono.error(WikiTopicNotFound())
                wikiRepository.save(it).thenReturn(topic)
            }
    }

    /**
     * This will fetch the topic, if found by the identifier & topic name
     *
     * @param id        the plugin identifier
     * @param name      the wiki topic name
     * @see             WikiTopic
     * @throws WikiNotFound if wiki is not found using the identifier
     * @throws WikiTopicNotFound if the wiki topic is not found using the name
     */
    fun getTopic(id: UUID, name: String): Mono<WikiTopic> {
        return wikiRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(WikiNotFound()) })
            .flatMap {
                val topic = it.getWiki(name) ?: return@flatMap Mono.error(WikiTopicNotFound())
                Mono.just(topic)
            }
    }

    /**
     * This will fetch all topics under the identifier
     *
     * @param id        the plugin identifier
     * @see             WikiTopic
     * @throws WikiNotFound if wiki is not found using the identifier
     */
    fun getTopics(id: UUID): Flux<WikiTopic> {
        return wikiRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(WikiNotFound()) })
            .flatMapMany { Flux.fromIterable(it.topics.values) }
    }

    /**
     * This will remove a topic, if found by the identifier & name
     *
     * @param id        the plugin identifier
     * @param name      the wiki topic name
     * @see             WikiTopic
     * @throws WikiNotFound if the wiki is not found using the identifier
     * @throws WikiTopicNotFound if the wiki is not found using the name
     */
    fun removeTopic(id: UUID, name: String): Mono<WikiTopic> {
        return wikiRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(WikiNotFound()) })
            .flatMap {
                val topic = it.getWiki(name) ?: return@flatMap Mono.error(WikiTopicNotFound())
                wikiRepository.save(it.removeWiki(name)).thenReturn(topic)
            }
    }
}