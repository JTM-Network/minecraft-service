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

    /**
     * Allow users to post suggestions for plugins.
     *
     * @param req       the http request.
     * @param dto       the suggestion data transfer object.
     * @return          the saved suggestion.
     * @throws          ClientIdNotFound if the header "CLIENT_ID" is null or blank.
     * @throws          NotAllowedToPost if the user has reached post limit.
     * @see             Suggestion
     */
    fun addSuggestion(req: ServerHttpRequest, dto: SuggestionDto): Mono<Suggestion> {
        val id = req.headers.getFirst("CLIENT_ID")
        if (id.isNullOrBlank()) return Mono.error { ClientIdNotFound() }
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

    /**
     * Allow users to update a suggestion.
     *
     * @param req       the http request.
     * @param id        the identifier.
     * @param dto       the suggestion data transfer object.
     * @return          the updated suggestion.
     * @throws          ClientIdNotFound if the header "CLIENT_ID" is null or blank.
     * @throws          SuggestionNotFound if the suggestion was not found.
     * @see             Suggestion
     */
    fun updateSuggestion(req: ServerHttpRequest, id: UUID, dto: SuggestionDto): Mono<Suggestion> {
        val clientId = req.headers.getFirst("CLIENT_ID") ?: return Mono.error { ClientIdNotFound() }
        return suggestionRepository.findByIdAndPluginIdAndOriginalPoster(id, dto.pluginId, clientId)
            .switchIfEmpty(Mono.defer { Mono.error(SuggestionNotFound()) })
            .flatMap { suggestionRepository.save(it.updateComment(dto.comment)) }
    }

    /**
     * Get suggestion by identifier.
     *
     * @param id        the identifier.
     * @return          the suggestion found.
     * @throws          SuggestionNotFound if the suggestion was not found by identifier.
     * @see             Suggestion
     */
    fun getSuggestion(id: UUID): Mono<Suggestion> {
        return suggestionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(SuggestionNotFound()) })
    }

    /**
     * Get suggestions under an account.
     *
     * @param request   the http request.
     * @return          the list of suggestions found.
     * @throws          ClientIdNotFound if the header "CLIENT_ID" was not found.
     * @see             Suggestion
     */
    fun getSuggestionsByAccount(request: ServerHttpRequest): Flux<Suggestion> {
        val id = request.headers.getFirst("CLIENT_ID") ?: return Flux.error { ClientIdNotFound() }
        return suggestionRepository.findByOriginalPoster(id)
    }

    /**
     * Get suggestions by account identifier.
     *
     * @param accountId the account identifier.
     * @return          the list of suggestions under the account.
     * @see             Suggestion
     */
    fun getSuggestionsByAccountId(accountId: String): Flux<Suggestion> {
        return suggestionRepository.findByOriginalPoster(accountId)
    }

    /**
     * Get suggestions by plugin identifier.
     *
     * @param pluginId  the plugin identifier.
     * @return          the list of suggestions under the plugin.
     * @see             Suggestion
     */
    fun getSuggestionsByPluginId(pluginId: UUID): Flux<Suggestion> {
        return suggestionRepository.findByPluginId(pluginId)
    }

    /**
     * Gets all suggestions saved.
     *
     * @return          the list of suggestions saved.
     * @see             Suggestion
     */
    fun getSuggestions(): Flux<Suggestion> = suggestionRepository.findAll()

    /**
     * Removes a suggestion by identifier.
     *
     * @param id        the identifier.
     * @return          the deleted suggestion.
     * @see             Suggestion
     */
    fun removeSuggestion(id: UUID): Mono<Suggestion> {
        return suggestionRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(SuggestionNotFound()) })
            .flatMap { suggestionRepository.delete(it).thenReturn(it) }
    }
}