package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.data.service.UpdateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/update")
class UpdateController @Autowired constructor(private val updateService: UpdateService) {

    @PutMapping("/{id}/name")
    fun putName(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = updateService.updateName(id, dto)

    @PutMapping("/{id}/basic-desc")
    fun putBasicDesc(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = updateService.updateBasicDesc(id, dto)

    @PutMapping("/{id}/desc")
    fun putDesc(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = updateService.updateDesc(id, dto)

    @PutMapping("/{id}/version")
    fun putVersion(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = updateService.updateVersion(id, dto)

    @PutMapping("/{id}/active")
    fun putActive(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = updateService.updateActive(id, dto)

    @PutMapping("/{id}/price")
    fun putPrice(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = updateService.updatePrice(id, dto)
}