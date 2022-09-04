package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.WikiTopicDto
import com.jtm.plugin.core.domain.model.WikiTopic
import com.jtm.plugin.data.service.WikiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/wiki")
class WikiController @Autowired constructor(private val wikiService: WikiService) {

    @PostMapping("/{id}")
    fun postTopic(@PathVariable id: UUID, @RequestBody dto: WikiTopicDto): Mono<WikiTopic> {
        return wikiService.insertTopic(id, dto)
    }

    @PutMapping("/{id}")
    fun putTopic(@PathVariable id: UUID, @RequestBody dto: WikiTopicDto): Mono<WikiTopic> {
        return wikiService.updateTopic(id, dto)
    }

    @GetMapping("/{id}")
    fun getTopic(@PathVariable id: UUID, @RequestParam("name") name: String): Mono<WikiTopic> {
        return wikiService.getTopic(id, name)
    }

    @GetMapping("/{id}/all")
    fun getTopics(@PathVariable id: UUID): Flux<WikiTopic> {
        return wikiService.getTopics(id)
    }

    @DeleteMapping("/{id}/{name}")
    fun deleteTopic(@PathVariable id: UUID, @PathVariable name: String): Mono<WikiTopic> {
        return wikiService.removeTopic(id, name)
    }
}