package com.jtm.plugin.entrypoint.controller.image

import com.jtm.plugin.core.domain.model.ImageInfo
import com.jtm.plugin.data.service.image.ImageService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@WebFluxTest(ImageController::class)
@AutoConfigureWebTestClient
class ImageControllerUnitTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var imageService: ImageService

    private val part: FilePart = mock()
    private val resource: Resource = mock()

    @Before
    fun setup() {
        `when`(resource.filename).thenReturn("test.png")
    }

    @Test
    fun postImage() {
        `when`(imageService.insertImage(part)).thenReturn(Mono.just(ImageInfo("url/test")))

        testClient.post()
            .uri("/images/upload")

        verify(imageService, times(1)).insertImage(anyOrNull())
        verifyNoMoreInteractions(imageService)
    }

    @Test
    fun getImage() {
        `when`(imageService.getImage(anyString())).thenReturn(Mono.just(resource))

        testClient.get()
            .uri("/images/test.png")

        verify(imageService, times(1)).getImage(anyString())
        verifyNoMoreInteractions(imageService)
    }

    @Test
    fun getImages() {
        `when`(imageService.getImages()).thenReturn(Flux.just("test"))

        testClient.get()
            .uri("/images/all")

        verify(imageService, times(1)).getImages()
        verifyNoMoreInteractions(imageService)
    }

    @Test
    fun removeImage() {
        `when`(imageService.removeImage(anyString())).thenReturn(Mono.empty())

        testClient.delete()
            .uri("/images/test.png")

        verify(imageService, times(1)).removeImage(anyString())
        verifyNoMoreInteractions(imageService)
    }
}