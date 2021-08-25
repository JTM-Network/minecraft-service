package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.usecase.file.FileHandler
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
import java.io.File
import java.util.*

@RunWith(SpringRunner::class)
class ImageServiceTest {

    private val fileHandler: FileHandler = mock()
    private val imageService = ImageService(fileHandler)
    private val part: FilePart = mock()
    private val file: File = mock()

    @Test
    fun uploadImageTest() {
        `when`(part.filename()).thenReturn("test.png")
        `when`(fileHandler.save(anyString(), anyOrNull(), anyString())).thenReturn(Mono.empty())

        val returned = imageService.uploadImage(UUID.randomUUID(), part)

        verify(part, times(1)).filename()
        verifyNoMoreInteractions(part)

        verify(fileHandler, times(1)).save(anyString(), anyOrNull(), anyString())
        verifyNoMoreInteractions(fileHandler)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.url).isInstanceOf(String::class.java) }
            .verifyComplete()
    }

    @Test
    fun getImageTest() {
        val file = this.javaClass.classLoader.getResource("test.png")

        `when`(fileHandler.fetch(anyString())).thenReturn(Mono.just(File(file.toURI())))

        val returned = imageService.getImage(UUID.randomUUID(), "test")

        verify(fileHandler, times(1)).fetch(anyString())
        verifyNoMoreInteractions(fileHandler)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.filename).isEqualTo("test.png") }
            .verifyComplete()
    }

    @Test
    fun getImagesTest() {
        `when`(file.name).thenReturn("test")
        `when`(fileHandler.listFiles(anyString())).thenReturn(Flux.just(file))

        val returned = imageService.getImages(UUID.randomUUID())

        verify(fileHandler, times(1)).listFiles(anyString())
        verifyNoMoreInteractions(fileHandler)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.name).isEqualTo("test") }
            .verifyComplete()
    }

    @Test
    fun deleteImageTest() {
        `when`(file.name).thenReturn("test")
        `when`(fileHandler.delete(anyString())).thenReturn(Mono.just(file))

        val returned = imageService.deleteImage(UUID.randomUUID(), "test")

        verify(fileHandler, times(1)).delete(anyString())
        verifyNoMoreInteractions(fileHandler)

        StepVerifier.create(returned)
            .assertNext { assertThat(it).isEqualTo("test") }
            .verifyComplete()
    }
}