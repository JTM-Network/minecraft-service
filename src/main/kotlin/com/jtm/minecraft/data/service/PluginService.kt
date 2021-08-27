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

    /**
     * Insert the plugin using values from {@link PluginDto}
     *
     * @param dto - the data transfer object
     * @throws PluginFound - if the plugin has been found with the same name
     * @return the inserted plugin
     */
    fun insertPlugin(dto: PluginDto): Mono<Plugin> {
        return pluginRepository.findByName(dto.name)
            .flatMap<Plugin?> { Mono.defer { Mono.error(PluginFound()) } }.cast(Plugin::class.java)
            .switchIfEmpty(Mono.defer { pluginRepository.save(Plugin(name = dto.name, description = dto.description)) })
    }

    /**
     * Update the plugin name found by identifier
     *
     * @param id - the plugin identifier
     * @param dto - the data transfer object
     * @throws PluginNotFound - if the plugin has not been found by the id
     * @return the updated plugin
     */
    fun updateName(id: UUID, dto: PluginDto): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error { PluginNotFound() } })
            .flatMap { pluginRepository.save(it.updateName(dto)) }
    }

    /**
     * Update the plugin description found by identifier
     *
     * @param id - the plugin identifier
     * @param dto - the data transfer object
     * @throws PluginNotFound - if the plugin has not been found by the id
     * @return the updated plugin
     */
    fun updateDesc(id: UUID, dto: PluginDto): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.updateDesc(dto)) }
    }

    /**
     * Update the price of the plugin found by identifier
     *
     * @param id - the plugin identifier
     * @param dto - the data transfer object
     * @throws PluginNotFound - if the plugin has not been found by the id
     * @return the updated plugin
     */
    fun updatePrice(id: UUID, dto: PluginDto): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.updatePrice(dto.price)) }
    }

    /**
     * Update the status of the plugin found by identifier
     *
     * @param id - the plugin identifier
     * @param dto - the data transfer object
     * @throws PluginNotFound - if the plugin has not been found by the id
     * @return the updated plugin
     */
    fun updateActive(id: UUID, dto: PluginDto): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.updateActive(dto.active)) }
    }

    /**
     * Update the version for the plugin
     *
     * @param id - the plugin identifier
     * @param version - the new plugin version
     * @throws PluginNotFound - if the plugin has not been found by the id
     * @return the updated plugin
     */
    fun updateVersion(id: UUID, version: String): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.updateVersion(version)) }
    }

    /**
     * Fetch the plugin found by the identifier
     *
     * @param id - the plugin identifier
     * @throws PluginNotFound - if the plugin has not been found by the id
     * @return the plugin
     */
    fun getPlugin(id: UUID): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
    }

    /**
     * Fetch the plugin found by the name
     *
     * @param name - the plugin name
     * @throws PluginNotFound - if the plugin has not been found by the name
     * @return the plugin
     */
    fun getPluginByName(name: String): Mono<Plugin> {
        return pluginRepository.findByName(name)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
    }

    /**
     * Fetch all the plugins
     *
     * @return list of the plugins
     */
    fun getPlugins(): Flux<Plugin> {
        return pluginRepository.findAll()
    }

    /**
     * Add sorting and convert into paginated list of plugins using {@link Pageable}
     *
     * @param page - the pagination information to sort the plugins found
     * @return the sorted paginated list of plugins
     */
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

    /**
     * Search for plugins using their names and if it contains the text of the search parameter.
     * Also adds pagination to the resulting list.
     *
     * @param search - the value in which to match plugin names with
     * @param page - the pagination information to sort the plugins found
     * @return the matching paginated list of plugins
     */
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

    /**
     * Remove plugin using the identifier
     *
     * @param id - the plugin identifier
     * @throws PluginNotFound - if the plugin has not been found
     * @return the deleted plugin
     */
    fun removePlugin(id: UUID): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.delete(it).thenReturn(it) }
    }
}