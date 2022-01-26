package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.core.domain.exception.plugin.FailedUpdatePlugin
import com.jtm.plugin.core.domain.exception.plugin.PluginNotFound
import com.jtm.plugin.core.usecase.repository.PluginRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UpdateService @Autowired constructor(private val pluginRepository: PluginRepository) {

    /**
     * This will update plugin name.
     *
     * @param dto   data transfer object to update plugin
     * @return      the updated plugin
     * @see         Plugin
     * @throws FailedUpdatePlugin if data transfer object value to update is null
     * @throws PluginNotFound if plugin is not found by identifier
     */
    fun updateName(dto: PluginDto): Mono<Plugin> {
        val name = dto.name ?: return Mono.error(FailedUpdatePlugin())
        return pluginRepository.findById(dto.id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.updateName(name)) }
    }

    /**
     * This will update plugin basic description
     *
     * @param dto   data transfer object to update plugin
     * @return      the updated plugin
     * @see         Plugin
     * @throws FailedUpdatePlugin if data transfer object value to update is null
     * @throws PluginNotFound if plugin is not found by identifier
     */
    fun updateBasicDesc(dto: PluginDto): Mono<Plugin> {
        val basicDescription = dto.basic_description ?: return Mono.error(FailedUpdatePlugin())
        return pluginRepository.findById(dto.id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.updateBasicDesc(basicDescription)) }
    }

    /**
     * This will update plugin description
     *
     * @param dto   data transfer object to update plugin
     * @return      the updated plugin
     * @see         Plugin
     * @throws FailedUpdatePlugin if data transfer object value to update is null
     * @throws PluginNotFound if plugin is not found by identifier
     */
    fun updateDesc(dto: PluginDto): Mono<Plugin> {
        val description = dto.description ?: return Mono.error(FailedUpdatePlugin())
        return pluginRepository.findById(dto.id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.updateDesc(description)) }
    }

    /**
     * This will update the plugin's latest version
     *
     * @param dto   data transfer object to update plugin
     * @return      the updated plugin
     * @see         Plugin
     * @throws FailedUpdatePlugin if data transfer object value to update is null
     * @throws PluginNotFound if plugin is not found by identifier
     */
    fun updateVersion(dto: PluginDto): Mono<Plugin> {
        val version = dto.version ?: return Mono.error(FailedUpdatePlugin())
        return pluginRepository.findById(dto.id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.updateVersion(version)) }
    }

    /**
     * This will update if the plugin will be shown to users or not.
     *
     * @param dto   data transfer object to update plugin
     * @return      the updated plugin
     * @see         Plugin
     * @throws FailedUpdatePlugin if data transfer object value to update is null
     * @throws PluginNotFound if plugin is not found by identifier
     */
    fun updateActive(dto: PluginDto): Mono<Plugin> {
        val active = dto.active ?: return Mono.error(FailedUpdatePlugin())
        return pluginRepository.findById(dto.id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.updateActive(active)) }
    }

    /**
     * This will update the plugin's price & also update the premium boolean value
     * to true if greater than 0.0
     *
     * @param dto   data transfer object to update plugin
     * @return      the updated plugin
     * @see         Plugin
     * @throws FailedUpdatePlugin if data transfer object value to update is null
     * @throws PluginNotFound if plugin is not found by identifier
     */
    fun updatePrice(dto: PluginDto): Mono<Plugin> {
        val price = dto.price ?: return Mono.error(FailedUpdatePlugin())
        return pluginRepository.findById(dto.id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.save(it.updatePrice(price)) }
    }
}