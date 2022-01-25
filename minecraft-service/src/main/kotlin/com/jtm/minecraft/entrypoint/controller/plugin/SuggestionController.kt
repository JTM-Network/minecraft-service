package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.dto.SuggestionDto
import com.jtm.minecraft.core.domain.entity.plugin.Suggestion
import com.jtm.minecraft.data.service.plugin.SuggestionService
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

    @PutMapping
    fun putSuggestionComment(request: ServerHttpRequest, @RequestBody dto: SuggestionDto): Mono<Suggestion> {
        return suggestionService.updateSuggestionComment(request, dto)
    }

    @GetMapping("/{id}")
    fun getSuggestion(@PathVariable id: UUID): Mono<Suggestion> {
        return suggestionService.getSuggestion(id)
    }

    @GetMapping("/plugin/{id}")
    fun getSuggestionByPlugin(@PathVariable id: UUID): Flux<Suggestion> {
        return suggestionService.getSuggestionsByPlugin(id)
    }

    @GetMapping("/account/{id}")
    fun getSuggestionByAccount(@PathVariable id: UUID): Flux<Suggestion> {
        return suggestionService.getSuggestionsByAccount(id)
    }

    @GetMapping("/all")
    fun getSuggestions(): Flux<Suggestion> {
        return suggestionService.getSuggestions()
    }

    @DeleteMapping("/{id}")
    fun deleteSuggestion(@PathVariable id: UUID): Mono<Suggestion> {
        return suggestionService.deleteSuggestion(id)
    }
}