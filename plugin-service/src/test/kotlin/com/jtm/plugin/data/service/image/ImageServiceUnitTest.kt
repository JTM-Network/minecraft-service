package com.jtm.plugin.data.service.image

import com.jtm.plugin.core.usecase.file.ImageHandler
import junit.framework.TestCase.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.File

@RunWith(SpringRunner::class)
class ImageServiceUnitTest {

    private val imageHandler: ImageHandler = mock()
    private val imageService = ImageService(imageHandler)

    private val part: FilePart = mock()
    private val file: File = mock()

    @Before
    fun setup() {
        `when`(file.name).thenReturn("test.png")
    }

    @Test
    fun insertImage() {
        `when`(imageHandler.save(anyOrNull())).thenReturn(Mono.just(file))

        val returned = imageService.insertImage(part)

        verify(imageHandler, times(1)).save(anyOrNull())
        verifyNoMoreInteractions(imageHandler)

        StepVerifier.create(returned)
            .assertNext { assertEquals(it.url, "https://api.jtm-network.com/images/test.png") }
            .verifyComplete()
    }

    @Test
    fun getImage() {
        `when`(imageHandler.fetch(anyString())).thenReturn(Mono.just(file))

        val returned = imageService.getImage("test.png")

        verify(imageHandler, times(1)).fetch(anyString())
        verifyNoMoreInteractions(imageHandler)

        StepVerifier.create(returned)
            .assertNext { assertEquals(it.filename, "test.png") }
            .verifyComplete()
    }

    @Test
    fun getImages() {
        `when`(imageHandler.list()).thenReturn(Flux.just(file))

        val returned = imageService.getImages()

        verify(imageHandler, times(1)).list()
        verifyNoMoreInteractions(imageHandler)

        StepVerifier.create(returned)
            .assertNext { assertEquals(it, "test.png") }
            .verifyComplete()
    }

    @Test
    fun removeImage() {
        `when`(imageHandler.delete(anyString())).thenReturn(Mono.just(file))

        val returned = imageService.removeImage("test")

        verify(imageHandler, times(1)).delete(anyString())
        verifyNoMoreInteractions(imageHandler)

        StepVerifier.create(returned)
            .verifyComplete()
    }
}