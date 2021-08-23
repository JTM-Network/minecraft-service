package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.data.service.plugin.DownloadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/download")
class DownloadController @Autowired constructor(private val downloadService: DownloadService) {

    @GetMapping("/{id}")
    fun downloadVersion(@PathVariable id: UUID, response: ServerHttpResponse): Mono<Resource> {
        return downloadService.downloadVersion(response, id)
    }
}