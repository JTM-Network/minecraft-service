package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.model.FileInfo
import com.jtm.version.core.domain.model.FolderInfo
import com.jtm.version.data.service.FileSystemService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(FileSystemController::class)
@AutoConfigureWebTestClient
class FileSystemControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var fileSystemService: FileSystemService

    private val fileInfo = FileInfo("test.jar", 1.54, "jar")
    private val folderInfo = FolderInfo("test", 5)

    @Test
    fun getVersions() {
        `when`(fileSystemService.getVersions(anyOrNull())).thenReturn(Flux.just(fileInfo))

        testClient.get()
            .uri("/filesystem/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].fileName").isEqualTo("test.jar")
            .jsonPath("$[0].size").isEqualTo(1.54)
            .jsonPath("$[0].extension").isEqualTo("jar")

        verify(fileSystemService, times(1)).getVersions(anyOrNull())
        verifyNoMoreInteractions(fileSystemService)
    }

    @Test
    fun getFiles() {
        `when`(fileSystemService.getFiles(anyString())).thenReturn(Flux.just(fileInfo))

        testClient.get()
            .uri("/filesystem/files?path=/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].fileName").isEqualTo("test.jar")
            .jsonPath("$[0].size").isEqualTo(1.54)
            .jsonPath("$[0].extension").isEqualTo("jar")

        verify(fileSystemService, times(1)).getFiles(anyString())
        verifyNoMoreInteractions(fileSystemService)
    }

    @Test
    fun getFolders() {
        `when`(fileSystemService.getFolders(anyString())).thenReturn(Flux.just(folderInfo))

        testClient.get()
            .uri("/filesystem/folders?path=/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].name").isEqualTo("test")
            .jsonPath("$[0].files").isEqualTo(5)

        verify(fileSystemService, times(1)).getFolders(anyString())
        verifyNoMoreInteractions(fileSystemService)
    }

    @Test
    fun deleteFile() {
        `when`(fileSystemService.removeFile(anyString())).thenReturn(Mono.just(fileInfo))

        testClient.delete()
            .uri("/filesystem/file?path=/test.jar")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.fileName").isEqualTo("test.jar")
            .jsonPath("$.size").isEqualTo(1.54)
            .jsonPath("$.extension").isEqualTo("jar")

        verify(fileSystemService, times(1)).removeFile(anyString())
        verifyNoMoreInteractions(fileSystemService)
    }

    @Test
    fun deleteFolder() {
        `when`(fileSystemService.removeFolder(anyString())).thenReturn(Mono.just(folderInfo))

        testClient.delete()
            .uri("/filesystem/folder?path=/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("test")
            .jsonPath("$.files").isEqualTo(5)

        verify(fileSystemService, times(1)).removeFolder(anyString())
        verifyNoMoreInteractions(fileSystemService)
    }
}