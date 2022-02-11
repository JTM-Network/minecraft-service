package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.BugDto
import com.jtm.plugin.core.domain.entity.Bug
import com.jtm.plugin.data.service.BugService
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
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(BugController::class)
@AutoConfigureWebTestClient
class BugControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var bugService: BugService

    private val bug = Bug(pluginId = UUID.randomUUID(), poster = "poster", serverVersion = "1.16", pluginVersion = "0.1", recreateComment = "How to recreate", happensComment = "What happens.")
    private val bugTwo = Bug(pluginId = UUID.randomUUID(), poster = "posterTwo", serverVersion = "1.17", pluginVersion = "0.5", recreateComment = "How to recreate #2", happensComment = "What happens. #2")
    private val dto = BugDto(pluginId =  UUID.randomUUID(), serverVersion = "1.16", pluginVersion = "0.1", recreateComment = "How to recreate", happensComment = "What happens.")

    @Test
    fun postBug() {
        `when`(bugService.addBug(anyOrNull(), anyOrNull())).thenReturn(Mono.just(bug))

        testClient.post()
            .uri("/bug")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(bug.pluginId.toString())
            .jsonPath("$.poster").isEqualTo(bug.poster)
            .jsonPath("$.serverVersion").isEqualTo("1.16")
            .jsonPath("$.pluginVersion").isEqualTo("0.1")

        verify(bugService, times(1)).addBug(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun getBug() {
        `when`(bugService.getBug(anyOrNull())).thenReturn(Mono.just(bug))

        testClient.get()
            .uri("/bug/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(bug.pluginId.toString())
            .jsonPath("$.poster").isEqualTo(bug.poster)
            .jsonPath("$.serverVersion").isEqualTo("1.16")
            .jsonPath("$.pluginVersion").isEqualTo("0.1")

        verify(bugService, times(1)).getBug(anyOrNull())
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun getBugsByPlugin() {
        `when`(bugService.getBugsByPluginId(anyOrNull())).thenReturn(Flux.just(bug, bugTwo))

        testClient.get()
            .uri("/bug/plugin/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(bug.pluginId.toString())
            .jsonPath("$[0].poster").isEqualTo(bug.poster)
            .jsonPath("$[0].serverVersion").isEqualTo("1.16")
            .jsonPath("$[0].pluginVersion").isEqualTo("0.1")
            .jsonPath("$[1].pluginId").isEqualTo(bugTwo.pluginId.toString())
            .jsonPath("$[1].poster").isEqualTo(bugTwo.poster)
            .jsonPath("$[1].serverVersion").isEqualTo("1.17")
            .jsonPath("$[1].pluginVersion").isEqualTo("0.5")

        verify(bugService, times(1)).getBugsByPluginId(anyOrNull())
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun getBugsByPoster() {
        `when`(bugService.getBugsByPoster(anyOrNull())).thenReturn(Flux.just(bug, bugTwo))

        testClient.get()
            .uri("/bug/poster")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(bug.pluginId.toString())
            .jsonPath("$[0].poster").isEqualTo(bug.poster)
            .jsonPath("$[0].serverVersion").isEqualTo("1.16")
            .jsonPath("$[0].pluginVersion").isEqualTo("0.1")
            .jsonPath("$[1].pluginId").isEqualTo(bugTwo.pluginId.toString())
            .jsonPath("$[1].poster").isEqualTo(bugTwo.poster)
            .jsonPath("$[1].serverVersion").isEqualTo("1.17")
            .jsonPath("$[1].pluginVersion").isEqualTo("0.5")

        verify(bugService, times(1)).getBugsByPoster(anyOrNull())
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun getBugsByPosterId() {
        `when`(bugService.getBugsByPosterId(anyString())).thenReturn(Flux.just(bug, bugTwo))

        testClient.get()
            .uri("/bug/poster/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(bug.pluginId.toString())
            .jsonPath("$[0].poster").isEqualTo(bug.poster)
            .jsonPath("$[0].serverVersion").isEqualTo("1.16")
            .jsonPath("$[0].pluginVersion").isEqualTo("0.1")
            .jsonPath("$[1].pluginId").isEqualTo(bugTwo.pluginId.toString())
            .jsonPath("$[1].poster").isEqualTo(bugTwo.poster)
            .jsonPath("$[1].serverVersion").isEqualTo("1.17")
            .jsonPath("$[1].pluginVersion").isEqualTo("0.5")

        verify(bugService, times(1)).getBugsByPosterId(anyString())
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun getBugs() {
        `when`(bugService.getBugs()).thenReturn(Flux.just(bug, bugTwo))

        testClient.get()
            .uri("/bug/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(bug.pluginId.toString())
            .jsonPath("$[0].poster").isEqualTo(bug.poster)
            .jsonPath("$[0].serverVersion").isEqualTo("1.16")
            .jsonPath("$[0].pluginVersion").isEqualTo("0.1")
            .jsonPath("$[1].pluginId").isEqualTo(bugTwo.pluginId.toString())
            .jsonPath("$[1].poster").isEqualTo(bugTwo.poster)
            .jsonPath("$[1].serverVersion").isEqualTo("1.17")
            .jsonPath("$[1].pluginVersion").isEqualTo("0.5")

        verify(bugService, times(1)).getBugs()
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun deleteBug() {
        `when`(bugService.removeBug(anyOrNull())).thenReturn(Mono.just(bug))

        testClient.delete()
            .uri("/bug/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(bug.pluginId.toString())
            .jsonPath("$.poster").isEqualTo(bug.poster)
            .jsonPath("$.serverVersion").isEqualTo("1.16")
            .jsonPath("$.pluginVersion").isEqualTo("0.1")

        verify(bugService, times(1)).removeBug(anyOrNull())
        verifyNoMoreInteractions(bugService)
    }
}