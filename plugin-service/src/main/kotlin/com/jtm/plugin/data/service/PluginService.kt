package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.core.domain.exception.plugin.FailedUpdatePlugin
import com.jtm.plugin.core.domain.exception.plugin.PluginFound
import com.jtm.plugin.core.domain.exception.plugin.PluginInformationNull
import com.jtm.plugin.core.domain.exception.plugin.PluginNotFound
import com.jtm.plugin.core.domain.model.PageSupport
import com.jtm.plugin.core.usecase.currency.PriceConverter
import com.jtm.plugin.core.usecase.repository.PluginRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*
import java.util.stream.Collectors

@Service
class PluginService @Autowired constructor(private val pluginRepository: PluginRepository, private val priceConverter: PriceConverter) {

    /**
     * This will insert a new plugin, if name is not found it will be successful.
     *
     * @param dto   data transfer object to create a new plugin.
     * @return      the inserted plugin
     * @see         Plugin
     * @throws      PluginInformationNull if data transfer object name is null
     * @throws      PluginFound if name is attached to another plugin
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
     * @param id            the identifier to find the plugin
     * @param currency      the currency it will use as the price converted from GBP
     * @return              the plugin found
     * @see                 Plugin
     * @throws              PluginNotFound if plugin is not found by identifier
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
     * THis will find the plugin by the name.
     *
     * @param name          the name of the plugin
     * @return              the plugin found
     * @see                 Plugin
     * @throws              PluginNotFound if plugin is not found by the name
     */
    fun getPluginByName(name: String): Mono<Plugin> {
        return pluginRepository.findByName(name)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
    }

    /**
     * This will find all the plugins stored.
     *
     * @param currency      the currency it will use as the price converted from GBP
     * @return              the plugins stored
     * @see                 Plugin
     */
    fun getPlugins(currency: String?): Flux<Plugin> {
        return pluginRepository.findAll()
            .map {
                if (currency == null) return@map it
                it.convertPrice(priceConverter, currency)
            }
    }

    /**
     * This will sort the plugins based on the {@link Pageable} values provided.
     *
     * @param currency      the currency it will use as the price converted from GBP
     * @param page          the pagination information to sort the plugins
     * @see                 Plugin
     * @return              the paginated list of plugins
     */
    fun getPluginsPaginated(currency: String?, page: Pageable): Mono<PageSupport<Plugin>> {
        return pluginRepository.findAll(page.sort)
            .filter { it.active }
            .map {
                if (currency == null) return@map it
                it.convertPrice(priceConverter, currency)
            }
            .collectList()
            .map { PageSupport(it.stream()
                    .skip(((page.pageNumber - 1) * page.pageSize).toLong())
                    .limit(page.pageSize.toLong())
                    .collect(Collectors.toList()),
                page.pageNumber,
                page.pageSize,
                it.size)
            }
    }

    /**
     * This will search for the plugins name that contains the search parameter and
     * return those found in a paginated list.
     *
     * @param search        the search value
     * @param currency      the currency it will use as the price converted from GBP
     * @param page          the pagination information to sort the plugin
     * @see                 Plugin
     * @return              the plugins that match the search in a paginated list.
     */
    fun getPluginsBySearch(search: String, currency: String?, page: Pageable): Mono<PageSupport<Plugin>> {
        return pluginRepository.findAll(page.sort)
            .filter { it.active }
            .map {
                if (currency == null) return@map it
                it.convertPrice(priceConverter, currency)
            }
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
     * This will delete the plugin found by the identifier.
     *
     * @param id    the identifier to find the plugin
     * @return      the plugin found
     * @see         Plugin
     * @throws      PluginNotFound if plugin is not found by identifier.
     */
    fun deletePlugin(id: UUID): Mono<Plugin> {
        return pluginRepository.findById(id)
            .switchIfEmpty(Mono.defer { Mono.error(PluginNotFound()) })
            .flatMap { pluginRepository.delete(it).thenReturn(it) }
    }
}