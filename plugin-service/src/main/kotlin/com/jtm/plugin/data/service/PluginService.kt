package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.core.domain.exception.plugin.FailedUpdatePlugin
import com.jtm.plugin.core.domain.exception.plugin.PluginFound
import com.jtm.plugin.core.domain.exception.plugin.PluginInformationNull
import com.jtm.plugin.core.domain.exception.plugin.PluginNotFound
import com.jtm.plugin.core.usecase.currency.PriceConverter
import com.jtm.plugin.core.usecase.repository.PluginRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class PluginService @Autowired constructor(private val pluginRepository: PluginRepository, private val priceConverter: PriceConverter) {

    /**
     * This will insert a new plugin, if name is not found it will be successful.
     *
     * @param dto   data transfer object to create a new plugin.
     * @return      the inserted plugin
     * @see         Plugin
     * @throws PluginInformationNull if data transfer object name is null
     * @throws PluginFound if name is attached to another plugin
     */
    fun insertPlugin(dto: PluginDto): Mono<Plugin> {
        val name = dto.name ?: return Mono.error(PluginInformationNull())
        dto.basic_description ?: return Mono.error(PluginInformationNull())
        dto.description ?: return Mono.error(PluginInformationNull())
        return pluginRepository.findByName(name)
            .flatMap<Plugin> { Mono.error(PluginFound()) }
            .switchIfEmpty(Mono.defer { pluginRepository.save(Plugin(dto)) })
    }

    /**
     * This will find the plugin by the identifier.
     *
     * @param id    the identifier to find the plugin
     * @return      the plugin found
     * @see         Plugin
     * @throws PluginNotFound if plugin is not found by identifier
     */
    fun getPlugin(id: UUID, currency: String?): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .map {
                if (currency != null) return@map it.convertPrice(priceConverter, currency)
                it
            }
    }

    /**
     * This will find all the plugins stored.
     *
     * @return      the plugins stored
     * @see         Plugin
     */
    fun getPlugins(currency: String?): Flux<Plugin> {
        return pluginRepository.findAll()
            .map {
                if (currency != null) it.convertPrice(priceConverter, currency)
                it
            }
    }

    /**
     * This will delete the plugin found by the identifier.
     *
     * @param id    the identifier to find the plugin
     * @return      the plugin found
     * @see         Plugin
     * @throws PluginNotFound if plugin is not found by identifier.
     */
    fun deletePlugin(id: UUID): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.delete(it).thenReturn(it) }
    }
}