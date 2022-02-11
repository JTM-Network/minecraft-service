package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.BugDto
import com.jtm.plugin.core.domain.entity.Bug
import com.jtm.plugin.data.service.BugService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/bug")
class BugController @Autowired constructor(private val bugService: BugService) {

    @PostMapping
    fun postBug(req: ServerHttpRequest, @RequestBody dto: BugDto): Mono<Bug> {
        return bugService.addBug(req, dto)
    }

    @GetMapping("/{id}")
    fun getBug(@PathVariable id: UUID): Mono<Bug> {
        return bugService.getBug(id)
    }

    @GetMapping("/plugin/{plugin}")
    fun getBugsByPlugin(@PathVariable plugin: UUID): Flux<Bug> {
        return bugService.getBugsByPluginId(plugin)
    }

    @GetMapping("/poster")
    fun getBugsByPoster(req: ServerHttpRequest): Flux<Bug> {
        return bugService.getBugsByPoster(req)
    }

    @GetMapping("/poster/{poster}")
    fun getBugsByPosterId(@PathVariable poster: String): Flux<Bug> {
        return bugService.getBugsByPosterId(poster)
    }

    @GetMapping("/all")
    fun getBugs(): Flux<Bug> {
        return bugService.getBugs()
    }

    @DeleteMapping("/{id}")
    fun deleteBug(@PathVariable id: UUID): Mono<Bug> {
        return bugService.removeBug(id)
    }
}