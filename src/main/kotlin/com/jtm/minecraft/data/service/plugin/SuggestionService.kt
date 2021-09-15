package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.SuggestionDto
import com.jtm.minecraft.core.domain.entity.plugin.Suggestion
import com.jtm.minecraft.core.domain.exceptions.plugin.suggestion.SuggestionFound
import com.jtm.minecraft.core.domain.exceptions.plugin.suggestion.SuggestionNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.repository.plugin.PluginSuggestionRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class SuggestionService @Autowired constructor(private val suggestionRepository: PluginSuggestionRepository, private val tokenProvider: AccountTokenProvider) {

    fun addSuggestion(request: ServerHttpRequest, dto: SuggestionDto): Mono<Suggestion> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error(InvalidJwtToken())
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error(InvalidJwtToken())
        return suggestionRepository.findByAccountIdAndPluginId(accountId, dto.pluginId)
                .flatMap<Suggestion?> { Mono.defer { Mono.error(SuggestionFound()) } }
                .switchIfEmpty(Mono.defer { suggestionRepository.save(Suggestion(accountId = accountId, dto = dto)) })
    }

    fun updateSuggestionComment(request: ServerHttpRequest, dto: SuggestionDto): Mono<Suggestion> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error(InvalidJwtToken())
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error(InvalidJwtToken())
        return suggestionRepository.findByAccountIdAndPluginId(accountId, dto.pluginId)
                .switchIfEmpty(Mono.defer { Mono.error(SuggestionNotFound()) })
                .flatMap { suggestionRepository.save(it.updateComment(dto.comment)) }
    }

    fun getSuggestion(id: UUID): Mono<Suggestion> {
        return suggestionRepository.findById(id)
                .switchIfEmpty(Mono.defer { Mono.error(SuggestionNotFound()) })
    }

    fun getSuggestionsByPlugin(pluginId: UUID): Flux<Suggestion> {
        return suggestionRepository.findByPluginId(pluginId)
    }

    fun getSuggestionsByAccount(accountId: UUID): Flux<Suggestion> {
        return suggestionRepository.findByAccountId(accountId)
    }

    fun getSuggestions(): Flux<Suggestion> {
        return suggestionRepository.findAll()
    }

    fun deleteSuggestion(id: UUID): Mono<Suggestion> {
        return suggestionRepository.findById(id)
                .switchIfEmpty(Mono.defer { Mono.error(SuggestionNotFound()) })
    }
}