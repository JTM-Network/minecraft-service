package com.jtm.version.data.service

import com.jtm.version.core.domain.entity.Version
import com.jtm.version.core.domain.exceptions.version.VersionNotFound
import com.jtm.version.core.usecase.repository.VersionRepository
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
class VersionServiceTest {

    private val versionRepository: VersionRepository = mock()
    private val versionService = VersionService(versionRepository)
    private val version = Version(pluginId = UUID.randomUUID(), pluginName = "test", version = "1.0", changelog = "Changelog")

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
    fun getVersion() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(version))

        val returned = versionService.getVersion(UUID.randomUUID())

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.pluginId).isEqualTo(version.pluginId)
                assertThat(it.version).isEqualTo("1.0")
                assertThat(it.changelog).isEqualTo("Changelog")
            }
            .verifyComplete()
    }

    @Test
    fun getPluginVersion_thenNotFound() {
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.empty())

        val returned = versionService.getPluginVersion(UUID.randomUUID(), "1.0")

        verify(versionRepository, times(1)).findByPluginIdAndVersion(anyOrNull(), anyString())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .expectError(VersionNotFound::class.java)
            .verify()
    }

    @Test
    fun getPluginVersion() {
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))

        val returned = versionService.getPluginVersion(UUID.randomUUID(), "1.0")

        verify(versionRepository, times(1)).findByPluginIdAndVersion(anyOrNull(), anyString())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.pluginId).isEqualTo(version.pluginId)
                assertThat(it.version).isEqualTo("1.0")
                assertThat(it.changelog).isEqualTo("Changelog")
            }
            .verifyComplete()
    }

    @Test
    fun getVersionsByPlugin() {
        `when`(versionRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(version))

        val returned = versionService.getVersionsByPlugin(UUID.randomUUID())

        verify(versionRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.pluginId).isEqualTo(version.pluginId)
                assertThat(it.version).isEqualTo("1.0")
                assertThat(it.changelog).isEqualTo("Changelog")
            }
            .verifyComplete()
    }

    @Test
    fun getVersions() {
        `when`(versionRepository.findAll()).thenReturn(Flux.just(version))

        val returned = versionService.getVersions()

        verify(versionRepository, times(1)).findAll()
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.pluginId).isEqualTo(version.pluginId)
                assertThat(it.version).isEqualTo("1.0")
                assertThat(it.changelog).isEqualTo("Changelog")
            }
            .verifyComplete()
    }

    @Test
    fun deleteVersion_thenNotFound() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = versionService.deleteVersion(UUID.randomUUID())

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .expectError(VersionNotFound::class.java)
            .verify()
    }

    @Test
    fun deleteVersion() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(version))
        `when`(versionRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = versionService.deleteVersion(UUID.randomUUID())

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.pluginId).isEqualTo(version.pluginId)
                assertThat(it.version).isEqualTo("1.0")
                assertThat(it.changelog).isEqualTo("Changelog")
            }
            .verifyComplete()
    }
}