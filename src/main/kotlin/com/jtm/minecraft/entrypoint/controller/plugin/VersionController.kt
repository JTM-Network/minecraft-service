package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.dto.PluginVersionDto
import com.jtm.minecraft.core.domain.entity.plugin.PluginVersion
import com.jtm.minecraft.core.domain.model.FolderInfo
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.plugin.AccessService
import com.jtm.minecraft.data.service.plugin.VersionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/version")
class VersionController @Autowired constructor(private val versionService: VersionService,
                                               private val fileHandler: FileHandler,
                                               private val accessService: AccessService,
                                               private val tokenProvider: AccountTokenProvider) {

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun postVersion(@RequestParam("pluginId") id: UUID, @RequestPart("version") version: String, @RequestPart("file") file: FilePart, @RequestPart("changelog") changelog: String): Mono<PluginVersion> {
        return versionService.insertVersion(PluginVersionDto(id, file, version, changelog), fileHandler)
    }

    @PutMapping
    fun putVersion(@RequestBody dto: PluginVersionDto): Mono<PluginVersion> {
        return versionService.updateVersion(dto)
    }

    @GetMapping("/download/request")
    fun getDownloadRequest(@RequestParam("name") name: String, @RequestParam("version") version: String, request: ServerHttpRequest): Mono<String> {
        return versionService.downloadVersionRequest(name, version, accessService, tokenProvider, request)
    }

    @GetMapping("/{id}")
    fun getVersion(@PathVariable id: UUID): Mono<PluginVersion> {
        return versionService.getVersion(id)
    }

    @GetMapping("/plugin/{id}")
    fun getVersionByPluginId(@PathVariable id: UUID): Flux<PluginVersion> {
        return versionService.getVersionsByPluginId(id)
    }

    @GetMapping("/name/{name}")
    fun getVersionByName(@PathVariable name: String): Flux<PluginVersion> {
        return versionService.getVersionsByPluginName(name)
    }

    @GetMapping("/all")
    fun getVersions(): Flux<PluginVersion> {
        return versionService.getVersions()
    }

    @DeleteMapping("/{id}")
    fun deleteVersion(@PathVariable id: UUID): Mono<PluginVersion> {
        return versionService.removeVersion(id)
    }

    @GetMapping("/folder/all")
    fun getFolderVersions(): Flux<FolderInfo> {
        return versionService.getFolderVersions(fileHandler)
    }

    @DeleteMapping("/folder/{id}")
    fun deleteFolderVersion(@PathVariable id: UUID): Mono<String> {
        return versionService.removeFolderVersion(id, fileHandler)
    }
}