package com.jtm.profile.entrypoint.controller

import com.jtm.profile.core.domain.entity.Profile
import com.jtm.profile.data.service.ProfileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class ProfileController @Autowired constructor(private val profileService: ProfileService) {

    @GetMapping("/me")
    fun getProfile(request: ServerHttpRequest): Mono<Profile> = profileService.getProfile(request)

    @DeleteMapping("/ban/{id}")
    fun banProfile(@PathVariable id: String): Mono<Profile> = profileService.banProfile(id)
}