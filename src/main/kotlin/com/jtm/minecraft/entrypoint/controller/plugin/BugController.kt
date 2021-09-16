package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.dto.BugDto
import com.jtm.minecraft.core.domain.entity.plugin.Bug
import com.jtm.minecraft.data.service.plugin.BugService
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
    fun postBug(request: ServerHttpRequest, @RequestBody dto: BugDto): Mono<Bug> {
        return bugService.addBug(request, dto)
    }

    @PutMapping
    fun putBugComment(request: ServerHttpRequest, @RequestBody dto: BugDto): Mono<Bug> {
        return bugService.updateBugComment(request, dto)
    }

    @GetMapping("/{id}")
    fun getBug(@PathVariable id: UUID): Mono<Bug> {
        return bugService.getBug(id)
    }

    @GetMapping("/plugin/{id}")
    fun getBugByPlugin(@PathVariable id: UUID): Flux<Bug> {
        return bugService.getBugByPlugin(id)
    }

    @GetMapping("/account/{id}")
    fun getBugByAccount(@PathVariable id: UUID): Flux<Bug> {
        return bugService.getBugByAccount(id)
    }

    @GetMapping("/all")
    fun getBugs(): Flux<Bug> {
        return bugService.getBugs()
    }

    @DeleteMapping("/{id}")
    fun deleteBug(@PathVariable id: UUID): Mono<Bug> {
        return bugService.deleteBug(id)
    }
}