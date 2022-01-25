package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.model.PluginIntent
import com.jtm.minecraft.data.service.plugin.IntentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/intent")
class IntentController @Autowired constructor(private val intentService: IntentService) {

    @PostMapping("/plugin")
    fun postIntent(request: ServerHttpRequest, @RequestBody intent: PluginIntent): Mono<String> {
        return intentService.createIntent(request, intent)
    }
}