package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.core.domain.model.PageSupport
import com.jtm.plugin.data.service.PluginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

    @GetMapping("/paginated")
    fun getPluginsPaginated(@RequestParam(name = "currency", required = false) currency: String?,
                            @RequestParam(name = "page", defaultValue = "1") page: Int,
                            @RequestParam(name = "size", defaultValue = "5") pageSize: Int,
                            @RequestParam(name = "sortBy", defaultValue = "createdTime") sortBy: String,
                            @RequestParam(name = "direction", defaultValue = "asc") direction: String): Mono<PageSupport<Plugin>> {
        return pluginService.getPluginsPaginated(currency, PageRequest.of(page, pageSize, Sort.by(Sort.Direction.valueOf(direction.uppercase(Locale.getDefault())), sortBy)))
    }

    @GetMapping("/search/{search}")
    fun getPluginsBySearch(@PathVariable search: String,
                           @RequestParam(name = "currency", required = false) currency: String?,
                           @RequestParam(name = "page", defaultValue = "1") page: Int,
                           @RequestParam(name = "size", defaultValue = "5") pageSize: Int): Mono<PageSupport<Plugin>> {
        return pluginService.getPluginsBySearch(search, currency, PageRequest.of(page, pageSize))
    }

    @DeleteMapping("/{id}")
    fun deletePlugin(@PathVariable id: UUID): Mono<Plugin> = pluginService.deletePlugin(id)
}