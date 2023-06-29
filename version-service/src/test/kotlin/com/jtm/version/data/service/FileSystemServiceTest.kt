package com.jtm.version.data.service

import com.jtm.version.core.domain.exceptions.filesystem.FileNotFound
import com.jtm.version.core.domain.exceptions.filesystem.FolderNotFound
import com.jtm.version.core.usecase.file.StandardFileSystemHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.File
import java.util.*

@RunWith(SpringRunner::class)
class FileSystemServiceTest {

    private val fileSystemHandler: StandardFileSystemHandler = mock()
    private val fileSystemService = FileSystemService(fileSystemHandler)
    private val fileOne: File = mock()
    private val folderOne: File = mock()

    @Before
    fun setup() {
        `when`(fileOne.path).thenReturn("/test.jar")
        `when`(fileOne.name).thenReturn("test.jar")
        `when`(fileOne.length()).thenReturn(1271343)
        `when`(fileOne.extension).thenReturn("test.jar")
        `when`(fileOne.isFile).thenReturn(true)

        `when`(folderOne.name).thenReturn("test")
        `when`(folderOne.listFiles()).thenReturn(arrayOf(fileOne))
        `when`(folderOne.isDirectory).thenReturn(true)
    }

    @Test
    fun getVersions() {
        `when`(standardFileSystemHandler.listFiles(anyString())).thenReturn(Flux.just(fileOne))

        val returned = fileSystemService.getVersions(UUID.randomUUID())

        verify(standardFileSystemHandler, times(1)).listFiles(anyString())
        verifyNoMoreInteractions(standardFileSystemHandler)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.fileName).isEqualTo("test.jar")
                assertThat(it.size).isEqualTo(1.21)
                assertThat(it.extension).isEqualTo("jar")
            }
            .verifyComplete()
    }

    @Test
    fun getFiles() {
        `when`(standardFileSystemHandler.listFiles(anyString())).thenReturn(Flux.just(fileOne))

        val returned = fileSystemService.getFiles("/test.jar")

        verify(standardFileSystemHandler, times(1)).listFiles(anyString())
        verifyNoMoreInteractions(standardFileSystemHandler)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.fileName).isEqualTo("test.jar")
                assertThat(it.size).isEqualTo(1.21)
                assertThat(it.extension).isEqualTo("jar")
            }
            .verifyComplete()
    }

    @Test
    fun getFolders() {
        `when`(standardFileSystemHandler.listFiles(anyString())).thenReturn(Flux.just(folderOne))

        val returned = fileSystemService.getFolders("/test")

        verify(standardFileSystemHandler, times(1)).listFiles(anyString())
        verifyNoMoreInteractions(standardFileSystemHandler)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test")
                assertThat(it.files).isEqualTo(1)
            }
            .verifyComplete()
    }

    @Test
    fun removeFile_thenNotFound() {
        `when`(standardFileSystemHandler.fetch(anyString())).thenReturn(Mono.just(folderOne))

        val returned = fileSystemService.removeFile("/test.jar")

        verify(standardFileSystemHandler, times(1)).fetch(anyString())
        verifyNoMoreInteractions(standardFileSystemHandler)

        StepVerifier.create(returned)
            .expectError(FileNotFound::class.java)
            .verify()
    }

    @Test
    fun removeFile() {
        `when`(standardFileSystemHandler.fetch(anyString())).thenReturn(Mono.just(fileOne))
        `when`(standardFileSystemHandler.delete(anyString())).thenReturn(Mono.just(fileOne))

        val returned = fileSystemService.removeFile("/test.jar")

        verify(standardFileSystemHandler, times(1)).fetch(anyString())
        verifyNoMoreInteractions(standardFileSystemHandler)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.fileName).isEqualTo("test.jar")
                assertThat(it.size).isEqualTo(1.21)
                assertThat(it.extension).isEqualTo("jar")
            }
            .verifyComplete()
    }

    @Test
    fun removeFolder_thenNotFound() {
        `when`(standardFileSystemHandler.fetch(anyString())).thenReturn(Mono.just(fileOne))

        val returned = fileSystemService.removeFolder("/test")

        verify(standardFileSystemHandler, times(1)).fetch(anyString())
        verifyNoMoreInteractions(standardFileSystemHandler)

        StepVerifier.create(returned)
            .expectError(FolderNotFound::class.java)
            .verify()
    }

    @Test
    fun removeFolder() {
        `when`(standardFileSystemHandler.fetch(anyString())).thenReturn(Mono.just(folderOne))
        `when`(standardFileSystemHandler.delete(anyString())).thenReturn(Mono.just(folderOne))

        val returned = fileSystemService.removeFolder("/test")

        verify(standardFileSystemHandler, times(1)).fetch(anyString())
        verifyNoMoreInteractions(standardFileSystemHandler)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.name).isEqualTo("test")
                assertThat(it.files).isEqualTo(1)
            }
            .verifyComplete()
    }
}