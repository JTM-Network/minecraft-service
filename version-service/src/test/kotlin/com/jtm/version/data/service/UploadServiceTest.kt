package com.jtm.version.data.service

import com.jtm.version.core.domain.dto.VersionDto
import com.jtm.version.core.domain.entity.Version
import com.jtm.version.core.domain.exceptions.filesystem.FileNotFound
import com.jtm.version.core.domain.exceptions.version.VersionFound
import com.jtm.version.core.usecase.file.StandardFileSystemHandler
import com.jtm.version.core.usecase.repository.VersionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.File
import java.util.*

@RunWith(SpringRunner::class)
class UploadServiceTest {

    private val versionRepository: VersionRepository = mock()
    private val systemHandler: StandardFileSystemHandler = mock()
    private val uploadService = UploadService(versionRepository, systemHandler)
    private val file: File = mock()
    private val filePart: FilePart = mock()
    private val dto = VersionDto(UUID.randomUUID(), "test", filePart, "0.1", "Changelog")
    private val dtoFileNull = VersionDto(UUID.randomUUID(), "test", null, "0.1", "Changelog")
    private val version = Version(pluginId = dto.pluginId, pluginName = dto.name, version = dto.version, changelog = dto.changelog)

    @Test
    fun uploadResource_thenVersionFound() {
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))

        val returned = uploadService.uploadResource(dto)

        verify(versionRepository, times(1)).findByPluginIdAndVersion(anyOrNull(), anyString())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .expectError(VersionFound::class.java)
            .verify()
    }

    @Test
    fun uploadResource_thenFileNotFound() {
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.empty())

        val returned = uploadService.uploadResource(dtoFileNull)

        verify(versionRepository, times(1)).findByPluginIdAndVersion(anyOrNull(), anyString())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .expectError(FileNotFound::class.java)
            .verify()
    }

    @Test
    fun uploadResource() {
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.empty())
        `when`(systemHandler.save(anyString(), anyOrNull(), anyString())).thenReturn(Mono.just(file))
        `when`(versionRepository.save(anyOrNull())).thenReturn(Mono.just(version))

        val returned = uploadService.uploadResource(dto)

        verify(versionRepository, times(1)).findByPluginIdAndVersion(anyOrNull(), anyString())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(version.id)
                assertThat(it.pluginName).isEqualTo("test")
                assertThat(it.changelog).isEqualTo("Changelog")
            }
            .verifyComplete()
    }
}