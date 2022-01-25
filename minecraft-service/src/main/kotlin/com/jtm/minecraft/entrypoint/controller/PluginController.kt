package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.dto.PluginDto
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.domain.model.PageSupport
import com.jtm.minecraft.data.service.PluginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/plugin")
class PluginController @Autowired constructor(private val pluginService: PluginService) {

    @PostMapping
    fun postPlugin(@RequestBody dto: PluginDto): Mono<Plugin> = pluginService.insertPlugin(dto)

    @PutMapping("/{id}/name")
    fun putPluginName(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = pluginService.updateName(id, dto)

    @PutMapping("/{id}/desc")
    fun putPluginDesc(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = pluginService.updateDesc(id, dto)

    @PutMapping("/{id}/price")
    fun putPluginPrice(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = pluginService.updatePrice(id, dto)

    @PutMapping("/{id}/active")
    fun putPluginActive(@PathVariable id: UUID, @RequestBody dto: PluginDto): Mono<Plugin> = pluginService.updateActive(id, dto)

    @GetMapping("/{id}")
    fun getPlugin(@PathVariable id: UUID): Mono<Plugin> = pluginService.getPlugin(id)

    @GetMapping("/name/{name}")
    fun getPluginByName(@PathVariable name: String): Mono<Plugin> = pluginService.getPluginByName(name)

    @GetMapping("/all")
    fun getPlugins(): Flux<Plugin> = pluginService.getPlugins()

    @GetMapping("/list")
    fun getPlugins(@RequestParam(name = "page", defaultValue = "1") page: Int,
                   @RequestParam(name = "size", defaultValue = "5") pageSize: Int,
                   @RequestParam(name = "sortBy", defaultValue = "createdTime") sortBy: String,
                   @RequestParam(name = "direction", defaultValue = "asc") direction: String): Mono<PageSupport<Plugin>> {
        return pluginService.getPluginsSortable(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.valueOf(direction.uppercase(Locale.getDefault())), sortBy)))
    }

    @GetMapping("/search/{search}")
    fun getPlugins(@PathVariable search: String,
                   @RequestParam(name = "page", defaultValue = "1") page: Int,
                   @RequestParam(name = "size", defaultValue = "5") pageSize: Int): Mono<PageSupport<Plugin>> {
        return pluginService.getPluginsBySearch(search, PageRequest.of(page, pageSize))
    }

    @DeleteMapping("/{id}")
    fun deletePlugin(@PathVariable id: UUID): Mono<Plugin> = pluginService.removePlugin(id)
}