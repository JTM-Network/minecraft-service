package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.BugDto
import com.jtm.plugin.core.domain.entity.Bug
import com.jtm.plugin.core.domain.exception.bug.BugNotFound
import com.jtm.plugin.core.domain.exception.profile.ClientIdNotFound
import com.jtm.plugin.core.domain.exception.profile.NotAllowedToPost
import com.jtm.plugin.core.usecase.repository.BugRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class BugService @Autowired constructor(private val bugRepository: BugRepository) {

    /**
     * Allow users to send a bug they have found. Using the client id in the header as the user id.
     *
     * @param req               the http request.
     * @param dto               the bug data transfer object.
     * @return                  the bug the user has found.
     * @see                     BugDto
     * @see                     Bug
     * @throws                  ClientIdNotFound if the header "CLIENT_ID" is null or blank.
     * @throws                  NotAllowedToPost if the user has reached his post limit.
     */
    fun addBug(req: ServerHttpRequest, dto: BugDto): Mono<Bug> {
        val id = req.headers.getFirst("CLIENT_ID")
        if (id.isNullOrBlank()) return Mono.error { ClientIdNotFound() }
        return bugRepository.findByPluginIdAndPoster(dto.pluginId, id)
            .switchIfEmpty(Mono.defer { bugRepository.save(Bug(id, dto)) })
            .sort(Comparator.comparing(Bug::posted).reversed())
            .take(1)
            .next()
            .flatMap {
                if (!it.canPost()) return@flatMap Mono.error(NotAllowedToPost())
                bugRepository.save(Bug(id, dto))
            }
    }

    /**
     * Get the bug by the identifier.
     *
     * @param id                the identifier
     * @return                  the bug found by the identifier
     * @see                     UUID
     * @see                     Bug
     * @throws                  BugNotFound if the bug is not found.
     */
    fun getBug(id: UUID): Mono<Bug> {
        return bugRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(BugNotFound()) })
    }

    /**
     * Get the bugs found by the plugin identifier.
     *
     * @param pluginId          the plugin identifier.
     * @return                  the list of bugs under the plugin identifier.
     * @see                     UUID
     * @see                     Bug
     */
    fun getBugsByPluginId(pluginId: UUID): Flux<Bug> {
        return bugRepository.findByPluginId(pluginId)
    }

    /**
     * Get the bugs found by the user using the client id in the header.
     *
     * @param req               the http request
     * @return                  the list of bugs under the user.
     * @see                     ServerHttpRequest
     * @see                     Bug
     * @throws                  ClientIdNotFound if the "CLIENT_ID" is null or blank.
     */
    fun getBugsByPoster(req: ServerHttpRequest): Flux<Bug> {
        val id = req.headers.getFirst("CLIENT_ID")
        if (id.isNullOrBlank()) return Flux.error(ClientIdNotFound())
        return bugRepository.findByPoster(id)
    }

    /**
     * Get the bugs found by the user id.
     *
     * @param poster            the poster id
     * @return                  the list of bugs found under the user.
     */
    fun getBugsByPosterId(poster: String): Flux<Bug> {
        return bugRepository.findByPoster(poster)
    }

    /**
     * Gets all bugs registered.
     *
     * @return                  the list of bugs
     * @see                     Bug
     */
    fun getBugs(): Flux<Bug> {
        return bugRepository.findAll()
    }

    /**
     * Remove the plugin by the identifier.
     *
     * @param id                the identifier.
     * @return                  the deleted plugin.
     * @see                     Bug
     * @throws                  BugNotFound if the bug has not been found by the identifier.
     */
    fun removeBug(id: UUID): Mono<Bug> {
        return bugRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(BugNotFound()) })
            .flatMap { bugRepository.delete(it).thenReturn(it) }
    }
}