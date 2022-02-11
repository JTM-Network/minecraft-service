package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.SuggestionDto
import com.jtm.plugin.core.domain.entity.Suggestion
import com.jtm.plugin.core.domain.exception.profile.ClientIdNotFound
import com.jtm.plugin.core.domain.exception.profile.NotAllowedToPost
import com.jtm.plugin.core.domain.exception.suggestion.SuggestionNotFound
import com.jtm.plugin.core.usecase.repository.SuggestionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class SuggestionService @Autowired constructor(private val suggestionRepository: SuggestionRepository) {

    fun addSuggestion(req: ServerHttpRequest, dto: SuggestionDto): Mono<Suggestion> {
        val id = req.headers.getFirst("CLIENT_ID") ?: return Mono.error { ClientIdNotFound() }
        return suggestionRepository.findByPluginIdAndOriginalPoster(dto.pluginId, id)
            .switchIfEmpty(Mono.defer { suggestionRepository.save(Suggestion(id, dto)) })
            .sort(Comparator.comparing(Suggestion::posted).reversed())
            .take(1)
            .next()
            .flatMap {
                if (!it.canPost()) return@flatMap Mono.error(NotAllowedToPost())
                suggestionRepository.save(Suggestion(id, dto))
            }
    }

    fun updateSuggestion(req: ServerHttpRequest, id: UUID, dto: SuggestionDto): Mono<Suggestion> {
        val clientId = req.headers.getFirst("CLIENT_ID") ?: return Mono.error { ClientIdNotFound() }
        return suggestionRepository.findByIdAndPluginIdAndOriginalPoster(id, dto.pluginId, clientId)
            .switchIfEmpty(Mono.defer { Mono.error(SuggestionNotFound()) })
            .flatMap { suggestionRepository.save(it.updateComment(dto.comment)) }
    }

    fun getSuggestion(id: UUID): Mono<Suggestion> {
        return suggestionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(SuggestionNotFound()) })
    }

    fun getSuggestionsByAccount(request: ServerHttpRequest): Flux<Suggestion> {
        val id = request.headers.getFirst("CLIENT_ID") ?: return Flux.error { ClientIdNotFound() }
        return suggestionRepository.findByOriginalPoster(id)
    }

    fun getSuggestionsByAccountId(accountId: String): Flux<Suggestion> {
        return suggestionRepository.findByOriginalPoster(accountId)
    }

    fun getSuggestionsByPluginId(pluginId: UUID): Flux<Suggestion> {
        return suggestionRepository.findByPluginId(pluginId)
    }

    fun getSuggestions(): Flux<Suggestion> = suggestionRepository.findAll()

    fun removeSuggestion(id: UUID): Mono<Suggestion> {
        return suggestionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(SuggestionNotFound()) })
            .flatMap { suggestionRepository.delete(it).thenReturn(it) }
    }
}