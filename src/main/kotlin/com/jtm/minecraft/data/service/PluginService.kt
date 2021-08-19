package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.dto.PluginDto
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginFound
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginNotFound
import com.jtm.minecraft.core.domain.model.PageSupport
import com.jtm.minecraft.core.usecase.repository.PluginRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.stream.Collectors

@Service
class PluginService @Autowired constructor(private val pluginRepository: PluginRepository) {

    fun insertPlugin(dto: PluginDto): Mono<Plugin> {
        return pluginRepository.findByName(dto.name)
            .flatMap<Plugin?> { Mono.defer { Mono.error(PluginFound()) } }.cast(Plugin::class.java)
            .switchIfEmpty(Mono.defer { pluginRepository.save(Plugin(name = dto.name, description = dto.description)) })
    }

    fun updatePlugin(id: UUID, dto: PluginDto): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.update(dto)) }
    }

    fun getPlugin(id: UUID): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
    }

    fun getPluginByName(name: String): Mono<Plugin> {
        return pluginRepository.findByName(name)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
    }

    fun getPlugins(): Flux<Plugin> {
        return pluginRepository.findAll()
    }

    fun getPluginsSortable(page: Pageable): Mono<PageSupport<Plugin>> {
        return pluginRepository.findAll(page.sort)
            .collectList()
            .map { PageSupport(
                it.stream()
                    .skip(((page.pageNumber - 1) * page.pageSize).toLong())
                    .limit(page.pageSize.toLong())
                    .collect(Collectors.toList()),
                page.pageNumber,
                page.pageSize,
                it.size) }
    }

    fun getPluginsBySearch(search: String, page: Pageable): Mono<PageSupport<Plugin>> {
        return pluginRepository.findAll(page.sort)
            .collectList()
            .map {
                val filtered = it.stream()
                    .filter { plugin -> plugin.name.contains(search) }
                    .skip(((page.pageNumber - 1) * page.pageSize).toLong())
                    .limit(page.pageSize.toLong())
                    .collect(Collectors.toList())
                PageSupport(filtered, page.pageNumber, page.pageSize, filtered.size)
            }
    }

    fun removePlugin(id: UUID): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.delete(it).thenReturn(it) }
    }
}