package com.jtm.profile.entrypoint.controller

import com.jtm.profile.core.domain.dto.AuthDto
import com.jtm.profile.data.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/authorize")
class AuthController @Autowired constructor(private val authService: AuthService) {

    @PostMapping("/check")
    fun isAuthorized(@RequestBody dto: AuthDto): Mono<Void> = authService.isAuthorized(dto.id, dto.plugin)
}