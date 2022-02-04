package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.dto.DownloadRequestDto
import com.jtm.version.data.service.DownloadRequestService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/request")
class DownloadRequestController @Autowired constructor(private val requestService: DownloadRequestService) {

    @PostMapping
    fun requestDownload(request: ServerHttpRequest, @RequestBody dto: DownloadRequestDto): Mono<UUID> {
        return requestService.requestDownload(request, dto)
    }
}