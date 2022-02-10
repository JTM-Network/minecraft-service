package com.jtm.profile.entrypoint.controller

import com.jtm.profile.core.domain.dto.AccessDto
import com.jtm.profile.data.service.AccessService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/access")
class AccessController @Autowired constructor(private val accessService: AccessService) {

    @PostMapping
    fun addAccess(@RequestBody dto: AccessDto): Mono<Void> {
        return accessService.addAccess(dto)
    }
}