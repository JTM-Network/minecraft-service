package com.jtm.plugin.core.usecase.repository

import com.jtm.plugin.core.domain.entity.Plugin
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface PluginRepository: ReactiveMongoRepository<Plugin, UUID> {

    fun findByName(name: String): Mono<Plugin>


}