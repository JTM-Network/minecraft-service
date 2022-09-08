package com.jtm.version.entrypoint.controller

import com.jtm.version.core.domain.dto.DownloadRequestDto
import com.jtm.version.core.domain.entity.DownloadLink
import com.jtm.version.data.service.DownloadRequestService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(DownloadRequestController::class)
@AutoConfigureWebTestClient
class DownloadRequestControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var requestService: DownloadRequestService

    private val id = UUID.randomUUID()
    private val dto = DownloadRequestDto(UUID.randomUUID(), "1.0")
    private val link = DownloadLink(pluginId = UUID.randomUUID(), version = "0.1", clientId = "ID")

    @Test
    fun requestDownload() {
        `when`(requestService.requestDownload(anyOrNull(), anyOrNull())).thenReturn(Mono.just(link))

        testClient.post()
            .uri("/request")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(link.id.toString())

        verify(requestService, times(1)).requestDownload(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(requestService)
    }

    @Test
    fun deleteDownload() {
        `when`(requestService.removeDownload(anyOrNull())).thenReturn(Mono.just(link))

        testClient.delete()
            .uri("/request/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(link.id.toString())
            .jsonPath("$.pluginId").isEqualTo(link.pluginId.toString())

        Mockito.verify(requestService, times(1)).removeDownload(anyOrNull())
        Mockito.verifyNoMoreInteractions(requestService)
    }
}