package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.SuggestionDto
import com.jtm.plugin.core.domain.entity.Suggestion
import com.jtm.plugin.data.service.SuggestionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/suggestion")
class SuggestionController @Autowired constructor(private val suggestionService: SuggestionService) {

    @PostMapping
    fun postSuggestion(request: ServerHttpRequest, @RequestBody dto: SuggestionDto): Mono<Suggestion> {
        return suggestionService.addSuggestion(request, dto)
    }

    @PutMapping("/{id}")
    fun putSuggestion(request: ServerHttpRequest, @PathVariable id: UUID, @RequestBody dto: SuggestionDto): Mono<Suggestion> {
        return suggestionService.updateSuggestion(request, id, dto)
    }

    @GetMapping("/{id}")
    fun getSuggestion(@PathVariable id: UUID): Mono<Suggestion> {
        return suggestionService.getSuggestion(id)
    }

    @GetMapping("/account")
    fun getSuggestionsByAccount(request: ServerHttpRequest): Flux<Suggestion> {
        return suggestionService.getSuggestionsByAccount(request)
    }

    @GetMapping("/account/{accountId}")
    fun getSuggestionsByAccountId(@PathVariable accountId: String): Flux<Suggestion> {
        return suggestionService.getSuggestionsByAccountId(accountId)
    }

    @GetMapping("/plugin/{pluginId}")
    fun getSuggestionsByPluginId(@PathVariable pluginId: UUID): Flux<Suggestion> {
        return suggestionService.getSuggestionsByPluginId(pluginId)
    }

    @GetMapping("/all")
    fun getSuggestions(): Flux<Suggestion> {
        return suggestionService.getSuggestions()
    }

    @DeleteMapping("/{id}")
    fun deleteSuggestion(@PathVariable id: UUID): Mono<Suggestion> {
        return suggestionService.removeSuggestion(id)
    }
}