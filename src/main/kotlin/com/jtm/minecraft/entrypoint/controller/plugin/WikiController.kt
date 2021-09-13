package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.dto.WikiTopicDto
import com.jtm.minecraft.core.domain.model.WikiTopic
import com.jtm.minecraft.data.service.plugin.WikiService
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

    @GetMapping("/{id}/{name}")
    fun getTopic(@PathVariable id: UUID, @PathVariable name: String): Mono<WikiTopic> {
        return wikiService.getTopic(id, name)
    }

    @GetMapping("/{id}")
    fun getTopics(@PathVariable id: UUID): Flux<WikiTopic> {
        return wikiService.getTopics(id)
    }

    @DeleteMapping("/{id}/{name}")
    fun deleteTopic(@PathVariable id: UUID, @PathVariable name: String): Mono<WikiTopic> {
        return wikiService.removeTopic(id, name)
    }
}