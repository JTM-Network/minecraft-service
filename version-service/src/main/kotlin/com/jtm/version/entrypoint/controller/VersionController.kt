package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.entity.Version
import com.jtm.version.data.service.VersionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping
class VersionController @Autowired constructor(private val versionService: VersionService) {

    @GetMapping("/{id}")
    fun getVersion(@PathVariable id: UUID): Mono<Version> = versionService.getVersion(id)

    @GetMapping("/plugin/{id}")
    fun getVersionByPluginId(@PathVariable id: UUID): Flux<Version> = versionService.getVersionsByPlugin(id)

    @GetMapping("/all")
    fun getVersions(): Flux<Version> = versionService.getVersions()

    @DeleteMapping("/{id}")
    fun deleteVersion(@PathVariable id: UUID): Mono<Version> = versionService.deleteVersion(id)
}