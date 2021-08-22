package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.PluginVersionDto
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.domain.entity.plugin.PluginVersion
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionFound
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionNotFound
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.repository.plugin.PluginVersionRepository
import com.jtm.minecraft.data.service.PluginService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class VersionServiceTest {

    private val pluginService: PluginService = mock()
    private val fileHandler: FileHandler = mock()
    private val versionRepository: PluginVersionRepository = mock()
    private val versionService = VersionService(pluginService, fileHandler, versionRepository)

    private val plugin = Plugin(name = "test", description = "desc")
    private val filePart: FilePart = mock()
    private val versionDto = PluginVersionDto(UUID.randomUUID(), filePart, "0.1", "changelog")
    private val version = PluginVersion(pluginId = UUID.randomUUID(), pluginName = "test", version = "0.1", changelog = "Changelog #1")

    @Test
    fun insertVersion_thenFound() {
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))

        val returned = versionService.insertVersion(versionDto)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .expectError(VersionFound::class.java)
            .verify()
    }

    @Test
    fun insertVersionTest() {
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.empty())
        `when`(versionRepository.save(anyOrNull())).thenReturn(Mono.just(version))

        val returned = versionService.insertVersion(versionDto)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(version.id)
                assertThat(it.version).isEqualTo(version.version)
                assertThat(it.changelog).isEqualTo(version.changelog)
            }
            .verifyComplete()
    }

    @Test
    fun updateVersion_thenNotFound() {
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyOrNull())).thenReturn(Mono.empty())

        val returned = versionService.updateVersion(versionDto)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .expectError(VersionNotFound::class.java)
            .verify()
    }

    @Test
    fun updateVersionTest() {
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyOrNull())).thenReturn(Mono.just(version))
        `when`(versionRepository.save(anyOrNull())).thenReturn(Mono.just(version))

        val returned = versionService.updateVersion(versionDto)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(version.id)
                assertThat(it.version).isEqualTo(version.version)
                assertThat(it.changelog).isEqualTo(version.changelog)
            }
            .verifyComplete()
    }

    @Test
    fun getVersion_thenNotFound() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = versionService.getVersion(UUID.randomUUID())

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .expectError(VersionNotFound::class.java)
            .verify()
    }

    @Test
    fun getVersionTest() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(version))

        val returned = versionService.getVersion(UUID.randomUUID())

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(version.id)
                assertThat(it.version).isEqualTo(version.version)
                assertThat(it.changelog).isEqualTo(version.changelog)
            }
            .verifyComplete()
    }

    @Test
    fun getVersionsByPluginIdTest() {
        `when`(versionRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(version))

        val returned = versionService.getVersionsByPluginId(UUID.randomUUID())

        verify(versionRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(version.id)
                assertThat(it.version).isEqualTo(version.version)
                assertThat(it.changelog).isEqualTo(version.changelog)
            }
            .verifyComplete()
    }

    @Test
    fun getVersionsByPluginNameTest() {
        `when`(versionRepository.findByPluginName(anyString())).thenReturn(Flux.just(version))

        val returned = versionService.getVersionsByPluginName("test")

        verify(versionRepository, times(1)).findByPluginName(anyString())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(version.id)
                assertThat(it.version).isEqualTo(version.version)
                assertThat(it.changelog).isEqualTo(version.changelog)
            }
            .verifyComplete()
    }

    @Test
    fun getVersionsTest() {
        `when`(versionRepository.findAll()).thenReturn(Flux.just(version))

        val returned = versionService.getVersions()

        verify(versionRepository, times(1)).findAll()
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(version.id)
                assertThat(it.version).isEqualTo(version.version)
                assertThat(it.changelog).isEqualTo(version.changelog)
            }
            .verifyComplete()
    }

    @Test
    fun removeVersion_thenNotFound() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = versionService.removeVersion(UUID.randomUUID())

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .expectError(VersionNotFound::class.java)
            .verify()
    }

    @Test
    fun removeVersionTest() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(version))
        `when`(versionRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = versionService.removeVersion(UUID.randomUUID())

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(version.id)
                assertThat(it.version).isEqualTo(version.version)
                assertThat(it.changelog).isEqualTo(version.changelog)
            }
            .verifyComplete()
    }
}