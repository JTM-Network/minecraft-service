package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.dto.DownloadRequestDto
import com.jtm.version.core.domain.entity.DownloadLink
import com.jtm.version.data.service.DownloadRequestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/request")
class DownloadRequestController @Autowired constructor(private val requestService: DownloadRequestService) {

    @PostMapping
    fun requestDownload(request: ServerHttpRequest, @RequestBody dto: DownloadRequestDto): Mono<DownloadLink> {
        return requestService.requestDownload(request, dto)
    }

    @DeleteMapping("/{id}")
    fun deleteDownload(@PathVariable id: UUID): Mono<DownloadLink> = requestService.removeDownload(id)
}