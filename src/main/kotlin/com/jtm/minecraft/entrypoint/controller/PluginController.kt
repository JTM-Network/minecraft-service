package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.dto.PluginDto
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.data.service.PluginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/plugin")
class PluginController @Autowired constructor(private val pluginService: PluginService) {

    @PostMapping
    fun postPlugin(@RequestBody dto: PluginDto): Mono<Plugin> = pluginService.insertPlugin(dto)

    @PutMapping("/{id}")
    fun putPlugin(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = pluginService.updatePlugin(id, dto)

    @GetMapping("/{id}")
    fun getPlugin(@PathVariable id: UUID): Mono<Plugin> = pluginService.getPlugin(id)

    @GetMapping("/name/{name}")
    fun getPluginByName(@PathVariable name: String): Mono<Plugin> = pluginService.getPluginByName(name)

    @GetMapping("/all")
    fun getPlugins(): Flux<Plugin> = pluginService.getPlugins()

    @DeleteMapping("/{id}")
    fun deletePlugin(@PathVariable id: UUID): Mono<Plugin> = pluginService.removePlugin(id)
}