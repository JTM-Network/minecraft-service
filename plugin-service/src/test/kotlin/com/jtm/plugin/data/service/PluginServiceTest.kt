package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.core.domain.exception.plugin.FailedUpdatePlugin
import com.jtm.plugin.core.domain.exception.plugin.PluginFound
import com.jtm.plugin.core.domain.exception.plugin.PluginInformationNull
import com.jtm.plugin.core.domain.exception.plugin.PluginNotFound
import com.jtm.plugin.core.usecase.currency.PriceConverter
import com.jtm.plugin.core.usecase.repository.PluginRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class PluginServiceTest {

    private val pluginRepository: PluginRepository = mock()
    private val converter: PriceConverter = mock()
    private val pluginService = PluginService(pluginRepository, converter)
    private val plugin = Plugin(name = "Test", basic_description = "Basic", description = "Desc", active = true)
    private val pluginTwo = Plugin(name = "plugin #3", basic_description = "Basic description #3", description = "description #3", active = true)
    private val dto = PluginDto(name = "Test #1", basic_description = "Basic description", description = "Description", version = "0.1", active = true, price = 10.50)
    private val nullDto = PluginDto(name = null, basic_description = null, description = null, version = null, active = null, price = null)

    @Test
    fun insertPlugin_thenPluginInfoNull() {
        val returned = pluginService.insertPlugin(nullDto)

        StepVerifier.create(returned)
            .expectError(PluginInformationNull::class.java)
            .verify()
    }

    @Test
    fun insertPlugin_thenFound() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.just(plugin))

        val returned = pluginService.insertPlugin(dto)

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginFound::class.java)
            .verify()
    }

    @Test
    fun insertPlugin() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.empty())
        `when`(pluginRepository.save(anyOrNull())).thenReturn(Mono.just(plugin))

        val returned = pluginService.insertPlugin(dto)

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
            }
            .verifyComplete()
    }

    @Test
    fun getPlugin_thenNotFound() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = pluginService.getPlugin(UUID.randomUUID(), null)

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun getPlugin() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(plugin))

        val returned = pluginService.getPlugin(UUID.randomUUID(), null)

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
                assertThat(it.version).isNull()
                assertThat(it.active).isTrue()
            }
            .verifyComplete()
    }

    @Test
    fun getPluginByName_thenNotFound() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.empty())

        val returned = pluginService.getPluginByName("test")

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun getPluginByName() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.just(plugin))

        val returned = pluginService.getPluginByName("test")

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
                assertThat(it.version).isNull()
                assertThat(it.active).isTrue()
            }
            .verifyComplete()
    }

    @Test
    fun getPlugins() {
        `when`(pluginRepository.findAll()).thenReturn(Flux.just(plugin, Plugin(name = "Test #2", basic_description = "Basic Desc #2", description = "Desc #2", version = "0.1", active = true)))

        val returned = pluginService.getPlugins(null)

        verify(pluginRepository, times(1)).findAll()
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
                assertThat(it.version).isNull()
                assertThat(it.active).isTrue()
            }
            .assertNext {
                assertThat(it.name).isEqualTo("Test #2")
                assertThat(it.basic_description).isEqualTo("Basic Desc #2")
                assertThat(it.description).isEqualTo("Desc #2")
                assertThat(it.version).isEqualTo("0.1")
                assertThat(it.active).isTrue
            }
            .verifyComplete()
    }

    @Test
    fun getPluginsPaginated() {
        val pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdTime"))

        `when`(pluginRepository.findAll(anyOrNull())).thenReturn(Flux.just(plugin, pluginTwo))

        val returned = pluginService.getPluginsPaginated(null, pageable)

        verify(pluginRepository, times(1)).findAll(anyOrNull())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.content[0].name).isEqualTo("Test")
                assertThat(it.content[0].basic_description).isEqualTo("Basic")
                assertThat(it.content[0].description).isEqualTo("Desc")

                assertThat(it.content[1].name).isEqualTo("plugin #3")
                assertThat(it.content[1].basic_description).isEqualTo("Basic description #3")
                assertThat(it.content[1].description).isEqualTo("description #3")

                assertThat(it.pageSize).isEqualTo(5)
                assertThat(it.pageNumber).isEqualTo(1)
                assertThat(it.totalElements).isEqualTo(2)
            }
            .verifyComplete()
    }

    @Test
    fun getPluginsBySearch() {
        val pageable = PageRequest.of(1, 5)

        `when`(pluginRepository.findAll(anyOrNull())).thenReturn(Flux.just(plugin, pluginTwo))

        val returned = pluginService.getPluginsBySearch("plu", null, pageable)

        verify(pluginRepository, times(1)).findAll(anyOrNull())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.content[0].name).isEqualTo("plugin #3")
                assertThat(it.content[0].basic_description).isEqualTo("Basic description #3")
                assertThat(it.content[0].description).isEqualTo("description #3")

                assertThat(it.pageSize).isEqualTo(5)
                assertThat(it.pageNumber).isEqualTo(1)
                assertThat(it.totalElements).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun deletePlugin_thenNotFound() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = pluginService.deletePlugin(UUID.randomUUID())

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun deletePlugin() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(plugin))
        `when`(pluginRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = pluginService.deletePlugin(UUID.randomUUID())

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
                assertThat(it.version).isNull()
                assertThat(it.active).isTrue()
            }
            .verifyComplete()
    }
}