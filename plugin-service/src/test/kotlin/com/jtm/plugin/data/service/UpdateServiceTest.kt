package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.PluginDto
import com.jtm.plugin.core.domain.entity.Plugin
import com.jtm.plugin.core.domain.exception.plugin.FailedUpdatePlugin
import com.jtm.plugin.core.domain.exception.plugin.PluginFound
import com.jtm.plugin.core.domain.exception.plugin.PluginNotFound
import com.jtm.plugin.core.usecase.repository.PluginRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class UpdateServiceTest {

    private val pluginRepository: PluginRepository = mock()
    private val updateService = UpdateService(pluginRepository)
    private val plugin = Plugin(name = "Test", basic_description = "Basic", description = "Desc")
    private val dto = PluginDto(id = UUID.randomUUID(), name = "Test #1", basic_description = "Basic description", description = "Description", version = "0.1", active = true, price = 10.50)
    private val nullDto = PluginDto(id = UUID.randomUUID(), name = null, basic_description = null, description = null, version = null, active = null, price = null)

    @Test
    fun updateName_thenFailedUpdate() {
        val returned = updateService.updateName(nullDto)

        StepVerifier.create(returned)
            .expectError(FailedUpdatePlugin::class.java)
            .verify()
    }

    @Test
    fun updateName_thenUpdatedNameFound() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.just(plugin))

        val returned = updateService.updateName(dto)

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginFound::class.java)
            .verify()
    }

    @Test
    fun updateName_thenNotFound() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.empty())
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = updateService.updateName(dto)

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun updateName() {
        `when`(pluginRepository.findByName(anyString())).thenReturn(Mono.empty())
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.just(plugin))
        `when`(pluginRepository.save(anyOrNull())).thenReturn(Mono.just(plugin))

        val returned = updateService.updateName(dto)

        verify(pluginRepository, times(1)).findByName(anyString())
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test #1")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
            }
            .verifyComplete()
    }

    @Test
    fun updateBasicDesc_thenFailedUpdate() {
        val returned = updateService.updateBasicDesc(nullDto)

        StepVerifier.create(returned)
            .expectError(FailedUpdatePlugin::class.java)
            .verify()
    }

    @Test
    fun updateBasicDesc_thenNotFound() {
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = updateService.updateBasicDesc(dto)

        verify(pluginRepository, times(1)).findById(ArgumentMatchers.any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun updateBasicDesc() {
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.just(plugin))
        `when`(pluginRepository.save(anyOrNull())).thenReturn(Mono.just(plugin))

        val returned = updateService.updateBasicDesc(dto)

        verify(pluginRepository, times(1)).findById(ArgumentMatchers.any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic description")
                assertThat(it.description).isEqualTo("Desc")
            }
            .verifyComplete()
    }

    @Test
    fun updateDesc_thenFailedUpdate() {
        val returned = updateService.updateDesc(nullDto)

        StepVerifier.create(returned)
            .expectError(FailedUpdatePlugin::class.java)
            .verify()
    }

    @Test
    fun updateDesc_thenNotFound() {
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = updateService.updateDesc(dto)

        verify(pluginRepository, times(1)).findById(ArgumentMatchers.any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun updateDesc() {
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.just(plugin))
        `when`(pluginRepository.save(anyOrNull())).thenReturn(Mono.just(plugin))

        val returned = updateService.updateDesc(dto)

        verify(pluginRepository, times(1)).findById(ArgumentMatchers.any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Description")
            }
            .verifyComplete()
    }

    @Test
    fun updateVersion_thenFailedUpdate() {
        val returned = updateService.updateVersion(nullDto)

        StepVerifier.create(returned)
            .expectError(FailedUpdatePlugin::class.java)
            .verify()
    }

    @Test
    fun updateVersion_thenNotFound() {
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = updateService.updateVersion(dto)

        verify(pluginRepository, times(1)).findById(ArgumentMatchers.any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun updateVersion() {
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.just(plugin))
        `when`(pluginRepository.save(anyOrNull())).thenReturn(Mono.just(plugin))

        val returned = updateService.updateVersion(dto)

        verify(pluginRepository, times(1)).findById(ArgumentMatchers.any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
                assertThat(it.version).isEqualTo("0.1")
            }
            .verifyComplete()
    }

    @Test
    fun updateActive_thenFailedUpdate() {
        val returned = updateService.updateActive(nullDto)

        StepVerifier.create(returned)
            .expectError(FailedUpdatePlugin::class.java)
            .verify()
    }

    @Test
    fun updateActive_thenNotFound() {
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = updateService.updateActive(dto)

        verify(pluginRepository, times(1)).findById(ArgumentMatchers.any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun updateActive() {
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.just(plugin))
        `when`(pluginRepository.save(anyOrNull())).thenReturn(Mono.just(plugin))

        val returned = updateService.updateActive(dto)

        verify(pluginRepository, times(1)).findById(ArgumentMatchers.any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
                assertThat(it.version).isNull()
                assertThat(it.active).isTrue
            }
            .verifyComplete()
    }

    @Test
    fun updatePrice_thenFailedUpdate() {
        val returned = updateService.updatePrice(nullDto)

        StepVerifier.create(returned)
            .expectError(FailedUpdatePlugin::class.java)
            .verify()
    }

    @Test
    fun updatePrice_thenNotFound() {
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = updateService.updatePrice(dto)

        verify(pluginRepository, times(1)).findById(ArgumentMatchers.any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .expectError(PluginNotFound::class.java)
            .verify()
    }

    @Test
    fun updatePrice() {
        `when`(pluginRepository.findById(ArgumentMatchers.any(UUID::class.java))).thenReturn(Mono.just(plugin))
        `when`(pluginRepository.save(anyOrNull())).thenReturn(Mono.just(plugin))

        val returned = updateService.updatePrice(dto)

        verify(pluginRepository, times(1)).findById(ArgumentMatchers.any(UUID::class.java))
        verifyNoMoreInteractions(pluginRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("Test")
                assertThat(it.basic_description).isEqualTo("Basic")
                assertThat(it.description).isEqualTo("Desc")
                assertThat(it.version).isNull()
                assertThat(it.active).isFalse
                assertThat(it.price).isEqualTo(10.50)
            }
            .verifyComplete()
    }
}