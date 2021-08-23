package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.PluginVersionDto
import com.jtm.minecraft.core.domain.entity.DownloadLink
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.domain.entity.Profile
import com.jtm.minecraft.core.domain.entity.plugin.PluginVersion
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionFound
import com.jtm.minecraft.core.domain.exceptions.plugin.version.VersionNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.file.FileHandler
import com.jtm.minecraft.core.usecase.repository.DownloadLinkRepository
import com.jtm.minecraft.core.usecase.repository.PluginRepository
import com.jtm.minecraft.core.usecase.repository.plugin.PluginVersionRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.PluginService
import com.jtm.minecraft.data.service.ProfileService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.HttpHeaders
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.File
import java.util.*

@RunWith(SpringRunner::class)
class VersionServiceTest {

    private val pluginService: PluginService = mock()
    private val pluginRepository: PluginRepository = mock()
    private val fileHandler: FileHandler = mock()
    private val downloadLinkRepository: DownloadLinkRepository = mock()
    private val versionRepository: PluginVersionRepository = mock()
    private val accessService: AccessService = mock()
    private val tokenProvider: AccountTokenProvider = mock()
    private val versionService = VersionService(pluginService, pluginRepository, versionRepository, downloadLinkRepository)

    private val plugin = Plugin(name = "test", description = "desc")
    private val filePart: FilePart = mock()
    private val versionDto = PluginVersionDto(UUID.randomUUID(), filePart, "0.1", "changelog")
    private val version = PluginVersion(pluginId = UUID.randomUUID(), pluginName = "test", version = "0.1", changelog = "Changelog #1")
    private val request: ServerHttpRequest = mock()
    private val headers: HttpHeaders = mock()
    private val file: File = mock()
    private val id = UUID.randomUUID()

    @Before
    fun setup() {
        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("Bearer test")
        `when`(file.name).thenReturn(id.toString())
        `when`(file.path).thenReturn("/test")
    }

    @Test
    fun insertVersion_thenFound() {
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))

        val returned = versionService.insertVersion(versionDto, fileHandler)

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
        `when`(fileHandler.save(anyString(), anyOrNull(), anyString())).thenReturn(Mono.empty())
        `when`(pluginService.updateVersion(anyOrNull(), anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(versionRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(version))

        val returned = versionService.insertVersion(versionDto, fileHandler)

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
    fun downloadVersionRequest_invalidAccountId() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = versionService.downloadVersionRequest(plugin.name, "0.1", accessService, tokenProvider, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun downloadVersionRequest_thenNotFound() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(accessService.hasAccess(anyString(), anyOrNull())).thenReturn(Mono.empty())
        `when`(pluginService.getPluginByName(anyString())).thenReturn(Mono.just(plugin))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyOrNull())).thenReturn(Mono.empty())

        val returned = versionService.downloadVersionRequest(plugin.name, "0.1", accessService, tokenProvider, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(accessService, times(1)).hasAccess(anyString(), anyOrNull())
        verifyNoMoreInteractions(accessService)

        StepVerifier.create(returned)
            .expectError(VersionNotFound::class.java)
            .verify()
    }

    @Test
    fun downloadVersionRequestTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(accessService.hasAccess(anyString(), anyOrNull())).thenReturn(Mono.empty())
        `when`(pluginService.getPluginByName(anyString())).thenReturn(Mono.just(plugin))
        `when`(versionRepository.findByPluginIdAndVersion(anyOrNull(), anyString())).thenReturn(Mono.just(version))
        `when`(downloadLinkRepository.save(anyOrNull())).thenReturn(Mono.just(DownloadLink(pluginId = plugin.id, version = version.version, accountId = UUID.randomUUID())))

        val returned = versionService.downloadVersionRequest(plugin.name, "0.1", accessService, tokenProvider, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(accessService, times(1)).hasAccess(anyString(), anyOrNull())
        verifyNoMoreInteractions(accessService)

        StepVerifier.create(returned)
            .assertNext { assertThat(it).isInstanceOf(String::class.java) }
            .verifyComplete()
    }

    @Test
    fun getLatestTest() {
        val versionTwo = PluginVersion(pluginId = version.pluginId, pluginName = version.pluginName, version = "0.2", changelog = "Changelog")

        `when`(versionRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(version, versionTwo))

        val returned = versionService.getLatest(UUID.randomUUID())

        verify(versionRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(versionRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.version).isEqualTo("0.2") }
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

    @Test
    fun getFolderVersionsTest() {
        `when`(fileHandler.listFiles(anyString())).thenReturn(Flux.just(file))

        val returned = versionService.getFolderVersions(fileHandler)

        verify(fileHandler, times(1)).listFiles(anyString())
        verifyNoMoreInteractions(fileHandler)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.name).isEqualTo(id.toString()) }
            .verifyComplete()
    }

    @Test
    fun removeFolderVersionTest() {
        `when`(fileHandler.delete(anyString())).thenReturn(Mono.just(file))

        val returned = versionService.removeFolderVersion(UUID.randomUUID(), fileHandler)

        verify(fileHandler, times(1)).delete(anyString())
        verifyNoMoreInteractions(fileHandler)

        StepVerifier.create(returned)
            .assertNext { assertThat(it).isEqualTo(id.toString()) }
            .verifyComplete()
    }
}