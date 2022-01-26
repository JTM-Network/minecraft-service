package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.data.service.UpdateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/update")
class UpdateController @Autowired constructor(private val updateService: UpdateService) {

    @PutMapping("/name")
    fun putName(@RequestBody dto: PluginDto): Mono<Plugin> = updateService.updateName(dto)

    @PutMapping("/basic-desc")
    fun putBasicDesc(@RequestBody dto: PluginDto): Mono<Plugin> = updateService.updateBasicDesc(dto)

    @PutMapping("/desc")
    fun putDesc(@RequestBody dto: PluginDto): Mono<Plugin> = updateService.updateDesc(dto)

    @PutMapping("/version")
    fun putVersion(@RequestBody dto: PluginDto): Mono<Plugin> = updateService.updateVersion(dto)

    @PutMapping("/active")
    fun putActive(@RequestBody dto: PluginDto): Mono<Plugin> = updateService.updateActive(dto)

    @PutMapping("/price")
    fun putPrice(@RequestBody dto: PluginDto): Mono<Plugin> = updateService.updatePrice(dto)
}