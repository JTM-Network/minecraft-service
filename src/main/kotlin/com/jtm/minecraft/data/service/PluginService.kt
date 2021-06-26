package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.dto.PluginDto
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.usecase.repository.PluginRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class PluginService @Autowired constructor(private val pluginRepository: PluginRepository) {

    fun insertPlugin(dto: PluginDto): Mono<Plugin> {
        return Mono.empty()
    }

    fun updatePlugin(dto: PluginDto): Mono<Plugin> {
        return Mono.empty()
    }

    fun getPlugin(id: UUID): Mono<Plugin> {
        return Mono.empty()
    }

    fun getPluginByName(name: String): Mono<Plugin> {
        return Mono.empty()
    }

    fun getPlugins(): Flux<Plugin> {
        return Flux.empty()
    }

    fun removePlugin(id: UUID): Mono<Plugin> {
        return Mono.empty()
    }
}