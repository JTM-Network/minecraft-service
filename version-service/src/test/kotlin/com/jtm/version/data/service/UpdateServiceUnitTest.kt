package com.jtm.version.data.service

import com.jtm.version.core.domain.dto.UpdateDto
import com.jtm.version.core.domain.dto.VersionDto
import com.jtm.version.core.domain.entity.Version
import com.jtm.version.core.domain.exceptions.version.VersionNotFound
import com.jtm.version.core.usecase.file.FileSystemHandler
import com.jtm.version.core.usecase.repository.VersionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class UpdateServiceUnitTest {

    private val versionRepository: VersionRepository = mock()
    private val fileHandler: FileSystemHandler = mock()
    private val updateService = UpdateService(versionRepository, fileHandler)

    private val dto = VersionDto(UUID.randomUUID(), "Test", version = "1.0", changelog = "Changelog")
    private val version = Version(dto)
    private val update = UpdateDto("1.1", "Changelog test")

    @Test
    fun updateVersion_shouldThrowNotFound() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = updateService.updateVersion(UUID.randomUUID(), update)

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .expectError(VersionNotFound::class.java)
            .verify()
    }

    @Test
    fun updateVersion_shouldReturnUpdated() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(version))
        `when`(versionRepository.save(anyOrNull())).thenReturn(Mono.just(version))
        `when`(fileHandler.updateFileName(anyString(), anyString())).thenReturn(Mono.empty())

        val returned = updateService.updateVersion(UUID.randomUUID(), update)

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(version.id)
                assertThat(it.pluginName).isEqualTo("Test")
                assertThat(it.version).isEqualTo("1.1")
                assertThat(it.changelog).isEqualTo("Changelog")
            }
            .verifyComplete()
    }

    @Test
    fun updateChangelog_shouldThrowNotFound() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = updateService.updateChangelog(UUID.randomUUID(), update)

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .expectError(VersionNotFound::class.java)
            .verify()
    }

    @Test
    fun updateChangelog_shouldReturnUpdated() {
        `when`(versionRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(version))
        `when`(versionRepository.save(anyOrNull())).thenReturn(Mono.just(version))

        val returned = updateService.updateChangelog(UUID.randomUUID(), update)

        verify(versionRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(version.id)
                assertThat(it.pluginName).isEqualTo("Test")
                assertThat(it.version).isEqualTo("1.0")
                assertThat(it.changelog).isEqualTo("Changelog test")
            }
            .verifyComplete()
    }
}