package com.jtm.minecraft.entrypoint.controller

import com.jtm.minecraft.data.service.HookService
import com.stripe.model.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/hook")
class HookController @Autowired constructor(private val hookService: HookService) {

    @PostMapping("/plugin")
    fun confirmAccess(@RequestBody event: Event): Mono<Void> {
        return hookService.addAccess(event)
    }
}