package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.data.service.PluginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
class PluginController @Autowired constructor(private val pluginService: PluginService) {

    @PostMapping
    fun postPlugin(@RequestBody dto: PluginDto): Mono<Plugin> = pluginService.insertPlugin(dto)

    @GetMapping("/{id}")
    fun getPlugin(@PathVariable id: UUID, @RequestParam("currency", required = false) currency: String?): Mono<Plugin> = pluginService.getPlugin(id, currency)

    @GetMapping("/all")
    fun getPlugins(@RequestParam("currency", required = false) currency: String?): Flux<Plugin> = pluginService.getPlugins(currency)

    @DeleteMapping("/{id}")
    fun deletePlugin(@PathVariable id: UUID): Mono<Plugin> = pluginService.deletePlugin(id)
}