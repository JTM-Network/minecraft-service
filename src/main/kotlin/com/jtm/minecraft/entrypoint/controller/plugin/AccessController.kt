package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.core.domain.model.PremiumDto
import com.jtm.minecraft.data.service.plugin.AccessService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/access")
class AccessController @Autowired constructor(private val accessService: AccessService) {

    @GetMapping("/{id}")
    fun addAccess(@PathVariable id: UUID, request: ServerHttpRequest): Mono<Void> {
        return accessService.addAccess(id, request)
    }

    @PostMapping("/premium")
    fun addPremiumAccess(@RequestBody dto: PremiumDto): Mono<Profile> {
        return accessService.addPremiumAccess(dto.accountId, dto.plugins)
    }

    @GetMapping("/check/{name}")
    fun hasAccess(@PathVariable name: String, request: ServerHttpRequest): Mono<Void> {
        return accessService.hasAccess(name, request)
    }

    @DeleteMapping
    fun removeAccess(@RequestParam("plugin") pluginId: UUID, @RequestParam("account") accountId: UUID): Mono<Profile> {
        return accessService.removeAccess(pluginId, accountId)
    }
}