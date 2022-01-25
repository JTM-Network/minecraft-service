package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.model.FolderInfo
import com.jtm.minecraft.core.domain.model.ImageInfo
import com.jtm.minecraft.data.service.plugin.ImageService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpEntity
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.http.codec.multipart.FilePart
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(ImageController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class ImageControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var imageService: ImageService

    @Test
    fun uploadImageTest() {
        `when`(imageService.uploadImage(anyOrNull(), anyOrNull())).thenReturn(Mono.just(ImageInfo("test")))

        testClient.post()
            .uri("/image/upload/${UUID.randomUUID()}")
            .body(BodyInserters.fromMultipartData(generateBody()))
            .exchange()
            .expectStatus().isOk

        verify(imageService, times(1)).uploadImage(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(imageService)
    }

    @Test
    fun getImageTest() {
        val file = this.javaClass.classLoader.getResource("test.png")
        val resource: Resource = FileSystemResource(File(file.toURI()))

        `when`(imageService.getImage(anyOrNull(), anyOrNull())).thenReturn(Mono.just(resource))

        testClient.get()
            .uri("/image/${UUID.randomUUID()}/test.png")
            .exchange()
            .expectStatus().isOk

        verify(imageService, times(1)).getImage(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(imageService)
    }

    @Test
    fun getImagesTest() {
        `when`(imageService.getImages(anyOrNull())).thenReturn(Flux.just(FolderInfo("test")))

        testClient.get()
            .uri("/image/all/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].name").isEqualTo("test")

        verify(imageService, times(1)).getImages(anyOrNull())
        verifyNoMoreInteractions(imageService)
    }

    @Test
    fun deleteImageTest() {
        `when`(imageService.deleteImage(anyOrNull(), anyString())).thenReturn(Mono.just("test"))

        testClient.delete()
            .uri("/image/${UUID.randomUUID()}/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isEqualTo("test")

        verify(imageService, times(1)).deleteImage(anyOrNull(), anyString())
        verifyNoMoreInteractions(imageService)
    }

    fun generateBody(): MultiValueMap<String, HttpEntity<*>> {
        val builder = MultipartBodyBuilder()
        builder.part("file", ClassPathResource("test.png"))
        return builder.build()
    }
}