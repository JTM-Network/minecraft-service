package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.dto.UpdateDto
import com.jtm.version.core.domain.entity.Version
import com.jtm.version.data.service.UpdateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/update")
class UpdateController @Autowired constructor(private val updateService: UpdateService) {

    @PutMapping("/{id}/version")
    fun putVersion(@PathVariable id: UUID, @RequestBody dto: UpdateDto): Mono<Version> = updateService.updateVersion(id, dto)

    @PutMapping("/{id}/changelog")
    fun putChangelog(@PathVariable id: UUID, @RequestBody dto: UpdateDto): Mono<Version> = updateService.updateChangelog(id, dto)
}