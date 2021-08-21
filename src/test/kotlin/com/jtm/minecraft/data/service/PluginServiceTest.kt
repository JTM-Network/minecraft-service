package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.dto.PluginDto
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginFound
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginNotFound
import com.jtm.minecraft.core.usecase.repository.PluginRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class PluginServiceTest {

    private val pluginRepository = mock(PluginRepository::class.java)
    private val pluginService = PluginService(pluginRepository)

    private val created = Plugin(name = "test", description = "test")

    @Test fun insertPluginTest() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.empty())
        `when`(pluginRepository.save(any(Plugin::class.java))).thenReturn(Mono.just(created))

        val returned = pluginService.insertPlugin(PluginDto("test", "test"))

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test")
                assertThat(it.description).isEqualTo("test")
                assertThat(it.createdTime).isLessThanOrEqualTo(System.currentTimeMillis())
            }
            .verifyComplete()
    }

    @Test fun insertPlugin_thenFoundTest() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.just(created))

        val returned = pluginService.insertPlugin(PluginDto("test", "test"))

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginFound::class.java)
            .verify()
    }

    @Test fun updatePluginTest() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))
        `when`(pluginRepository.save(any(Plugin::class.java))).thenReturn(Mono.just(created))

        val returned = pluginService.updatePlugin(UUID.randomUUID(), PluginDto("test", "test"))

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test")
                assertThat(it.description).isEqualTo("test")
            }
            .verifyComplete()
    }

    @Test fun updatePlugin_thenNotFoundTest() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = pluginService.updatePlugin(UUID.randomUUID(), PluginDto("test", "test"))

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test fun getPluginTest() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))

        val returned = pluginService.getPlugin(UUID.randomUUID())

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test")
                assertThat(it.description).isEqualTo("test")
            }
            .verifyComplete()
    }

    @Test fun getPlugin_thenNotFoundTest() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = pluginService.getPlugin(UUID.randomUUID())

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test fun getPluginByNameTest() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.just(created))

        val returned = pluginService.getPluginByName("test")

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test")
                assertThat(it.description).isEqualTo("test")
            }
            .verifyComplete()
    }

    @Test fun getPluginByName_thenNotFoundTest() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.empty())

        val returned = pluginService.getPluginByName("test")

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test fun getPluginsTest() {
        `when`(pluginRepository.findAll()).thenReturn(Flux.just(created, Plugin(name = "test #2", description = "test #3")))

        val returned = pluginService.getPlugins()

        verify(pluginRepository, times(1)).findAll()
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test")
                assertThat(it.description).isEqualTo("test")
            }
            .assertNext {
                assertThat(it.name).isEqualTo("test #2")
                assertThat(it.description).isEqualTo("test #3")
            }
            .verifyComplete()
    }

    @Test
    fun getPluginsSortableTest() {
        val pageable: Pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdTime"))

        `when`(pluginRepository.findAll(anyOrNull())).thenReturn(Flux.just(created, Plugin(name = "test #3", description = "desc #3")))

        val returned = pluginService.getPluginsSortable(pageable)

        verify(pluginRepository, times(1)).findAll(anyOrNull())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.content[0].name).isEqualTo("test")
                assertThat(it.content[0].description).isEqualTo("test")

                assertThat(it.content[1].name).isEqualTo("test #3")
                assertThat(it.content[1].description).isEqualTo("desc #3")

                assertThat(it.pageSize).isEqualTo(5)
                assertThat(it.pageNumber).isEqualTo(1)
                assertThat(it.totalElements).isEqualTo(2)
            }
            .verifyComplete()
    }

    @Test
    fun getPluginsBySearchTest() {
        val pageable: Pageable = PageRequest.of(1, 5, Sort.by(Sort.Direction.ASC, "createdTime"))

        `when`(pluginRepository.findAll(anyOrNull())).thenReturn(Flux.just(created, Plugin(name = "test #3", description = "desc #3"), Plugin(name = "plugin", description = "plugin desc")))

        val returned = pluginService.getPluginsBySearch("plu", pageable)

        verify(pluginRepository, times(1)).findAll(anyOrNull())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.content[0].name).isEqualTo("plugin")
                assertThat(it.content[0].description).isEqualTo("plugin desc")

                assertThat(it.pageSize).isEqualTo(5)
                assertThat(it.pageNumber).isEqualTo(1)
                assertThat(it.totalElements).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun removePluginTest() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))
        `when`(pluginRepository.delete(any(Plugin::class.java))).thenReturn(Mono.empty())

        val returned = pluginService.removePlugin(UUID.randomUUID())

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test")
                assertThat(it.description).isEqualTo("test")
            }
            .verifyComplete()
    }

    @Test fun removePlugin_thenNotFoundTest() {
        `when`(pluginRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = pluginService.removePlugin(UUID.randomUUID())

        verify(pluginRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }
}