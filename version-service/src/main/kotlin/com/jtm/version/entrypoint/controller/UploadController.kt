package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.dto.VersionDto
import com.jtm.version.core.domain.entity.Version
import com.jtm.version.data.service.UploadService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/upload")
class UploadController @Autowired constructor(private val uploadService: UploadService) {

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadResource(@RequestParam("pluginId") pluginId: UUID, @RequestParam("name") name: String,
                       @RequestPart("file") file: FilePart, @RequestPart("version") version: String,
                       @RequestPart("changelog") changelog: String): Mono<Version> {
        return uploadService.uploadResource(VersionDto(pluginId, name, file, version, changelog))
    }
}