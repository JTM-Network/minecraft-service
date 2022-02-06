package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.dto.DownloadRequestDto
import com.jtm.version.core.domain.entity.DownloadLink
import com.jtm.version.data.service.DownloadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/download")
class DownloadController @Autowired constructor(private val downloadService: DownloadService) {

    @GetMapping("/{id}")
    fun getDownload(response: ServerHttpResponse, @PathVariable id: UUID): Mono<Resource> = downloadService.getDownload(response, id)
}