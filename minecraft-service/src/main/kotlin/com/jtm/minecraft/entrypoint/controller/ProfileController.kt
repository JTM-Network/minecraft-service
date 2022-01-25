package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.data.service.ProfileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/profile")
class ProfileController @Autowired constructor(private val profileService: ProfileService) {

    @GetMapping("/id/{id}")
    fun getProfile(@PathVariable id: UUID): Mono<Profile> {
        return profileService.getProfile(id)
    }

    @GetMapping("/me")
    fun getProfileByBearer(request: ServerHttpRequest): Mono<Profile> {
        return profileService.getProfileByBearer(request)
    }

    @GetMapping("/all")
    fun getProfiles(): Flux<Profile> {
        return profileService.getProfiles()
    }

    @DeleteMapping("/{id}")
    fun deleteProfile(@PathVariable id: UUID): Mono<Profile> {
        return profileService.deleteProfile(id)
    }
}