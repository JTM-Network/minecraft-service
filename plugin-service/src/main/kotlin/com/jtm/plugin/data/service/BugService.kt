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

    fun addBug(req: ServerHttpRequest, dto: BugDto): Mono<Bug> {
        val id = req.headers.getFirst("CLIENT_ID") ?: return Mono.error { ClientIdNotFound() }
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

    fun getBug(id: UUID): Mono<Bug> {
        return bugRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(BugNotFound()) })
    }

    fun getBugsByPluginId(pluginId: UUID): Flux<Bug> {
        return bugRepository.findByPluginId(pluginId)
    }

    fun getBugsByPoster(req: ServerHttpRequest): Flux<Bug> {
        val id = req.headers.getFirst("CLIENT_ID") ?: return Flux.error(ClientIdNotFound())
        return bugRepository.findByPoster(id)
    }

    fun getBugsByPosterId(poster: String): Flux<Bug> {
        return bugRepository.findByPoster(poster)
    }

    fun getBugs(): Flux<Bug> {
        return bugRepository.findAll()
    }

    fun removeBug(id: UUID): Mono<Bug> {
        return bugRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(BugNotFound()) })
            .flatMap { bugRepository.delete(it).thenReturn(it) }
    }
}