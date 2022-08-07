package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.entity.Version
import com.jtm.version.data.service.VersionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/version")
class VersionController @Autowired constructor(private val versionService: VersionService) {

    @GetMapping("/{id}")
    fun getVersion(@PathVariable id: UUID): Mono<Version> = versionService.getVersion(id)

    @GetMapping("/plugin/{id}/{version}")
    fun getPluginVersion(@PathVariable id: UUID, @PathVariable version: String): Mono<Version> {
        return versionService.getPluginVersion(id, version)
    }

    @GetMapping("/plugin/{id}")
    fun getVersionByPluginId(@PathVariable id: UUID): Flux<Version> = versionService.getVersionsByPlugin(id)

    @GetMapping("/plugin/{id}/latest")
    fun getLatestVersion(@PathVariable id: UUID): Mono<Version> = versionService.getLatestVersion(id)

    @GetMapping("/all")
    fun getVersions(): Flux<Version> = versionService.getVersions()

    @DeleteMapping("/{id}")
    fun deleteVersion(@PathVariable id: UUID): Mono<Version> = versionService.deleteVersion(id)
}