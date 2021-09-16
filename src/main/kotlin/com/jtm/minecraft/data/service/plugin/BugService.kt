package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.BugDto
import com.jtm.minecraft.core.domain.entity.plugin.Bug
import com.jtm.minecraft.core.domain.exceptions.plugin.bug.BugFound
import com.jtm.minecraft.core.domain.exceptions.plugin.bug.BugNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.repository.plugin.BugRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class BugService @Autowired constructor(private val bugRepository: BugRepository, private val tokenProvider: AccountTokenProvider) {

    fun addBug(request: ServerHttpRequest, dto: BugDto): Mono<Bug> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error(InvalidJwtToken())
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error(InvalidJwtToken())
        return bugRepository.findByPluginIdAndAccountId(dto.pluginId, accountId)
                .flatMap<Bug?> { Mono.defer { Mono.error(BugFound()) } }
                .switchIfEmpty(Mono.defer { bugRepository.save(Bug(accountId = accountId, dto)) })
    }

    fun updateBugComment(request: ServerHttpRequest, dto: BugDto): Mono<Bug> {
        val bearer = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return Mono.error(InvalidJwtToken())
        val token = tokenProvider.resolveToken(bearer)
        val accountId = tokenProvider.getAccountId(token) ?: return Mono.error(InvalidJwtToken())
        return bugRepository.findByPluginIdAndAccountId(dto.pluginId, accountId)
                .switchIfEmpty(Mono.defer { Mono.error(BugNotFound()) })
                .flatMap { bugRepository.save(it.updateComment(dto.comment)) }
    }

    fun getBug(id: UUID): Mono<Bug> {
        return bugRepository.findById(id)
                .switchIfEmpty(Mono.defer { Mono.error(BugNotFound()) })
    }

    fun getBugByPlugin(pluginId: UUID): Flux<Bug> {
        return bugRepository.findByPluginId(pluginId)
    }

    fun getBugByAccount(accountId: UUID): Flux<Bug> {
        return bugRepository.findByAccountId(accountId)
    }

    fun getBugs(): Flux<Bug> {
        return bugRepository.findAll()
    }

    fun deleteBug(id: UUID): Mono<Bug> {
        return bugRepository.findById(id)
                .switchIfEmpty(Mono.defer { Mono.error(BugNotFound()) })
                .flatMap { bugRepository.delete(it).thenReturn(it) }
    }
}