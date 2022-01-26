package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.core.domain.exception.plugin.FailedUpdatePlugin
import com.jtm.plugin.core.domain.exception.plugin.PluginFound
import com.jtm.plugin.core.domain.exception.plugin.PluginInformationNull
import com.jtm.plugin.core.domain.exception.plugin.PluginNotFound
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
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class PluginServiceTest {

    private val pluginRepository: PluginRepository = mock()
    private val pluginService = PluginService(pluginRepository)
    private val plugin = Plugin(name = "Test", basic_description = "Basic", description = "Desc")
    private val dto = PluginDto(id = UUID.randomUUID(), name = "Test #1", basic_description = "Basic description", description = "Description", version = "0.1", active = true, price = 10.50)
    private val nullDto = PluginDto(id = UUID.randomUUID(), name = null, basic_description = null, description = null, version = null, active = null, price = null)

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

        val returned = pluginService.getPlugin(UUID.randomUUID())

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun getPlugin() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(plugin))

        val returned = pluginService.getPlugin(UUID.randomUUID())

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
                assertThat(it.version).isNull()
                assertThat(it.active).isFalse
            }
            .verifyComplete()
    }

    @Test
    fun getPlugins() {
        `when`(pluginRepository.findAll()).thenReturn(Flux.just(plugin, Plugin(name = "Test #2", basic_description = "Basic Desc #2", description = "Desc #2", version = "0.1", active = true)))

        val returned = pluginService.getPlugins()

        verify(pluginRepository, times(1)).findAll()
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
                assertThat(it.version).isNull()
                assertThat(it.active).isFalse
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
                assertThat(it.active).isFalse
            }
            .verifyComplete()
    }
}