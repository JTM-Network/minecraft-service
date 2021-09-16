package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.dto.BugDto
import com.jtm.minecraft.core.domain.entity.plugin.Bug
import com.jtm.minecraft.data.service.plugin.BugService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(BugController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class BugControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var bugService: BugService

    private val created = Bug(accountId = UUID.randomUUID(), pluginId = UUID.randomUUID(), comment = "test")
    private val dto = BugDto(pluginId = UUID.randomUUID(), "test comment")

    @Test
    fun postBug() {
        `when`(bugService.addBug(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.post()
                .uri("/bug")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$.comment").isEqualTo("test")

        verify(bugService, times(1)).addBug(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun putBugComment() {
        `when`(bugService.updateBugComment(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
                .uri("/bug")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$.comment").isEqualTo("test")

        verify(bugService, times(1)).updateBugComment(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun getBug() {
        `when`(bugService.getBug(anyOrNull())).thenReturn(Mono.just(created))

        testClient.get()
                .uri("/bug/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$.comment").isEqualTo("test")

        verify(bugService, times(1)).getBug(anyOrNull())
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun getBugByPlugin() {
        `when`(bugService.getBugByPlugin(anyOrNull())).thenReturn(Flux.just(created))

        testClient.get()
                .uri("/bug/plugin/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$[0].accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$[0].comment").isEqualTo("test")

        verify(bugService, times(1)).getBugByPlugin(anyOrNull())
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun getBugByAccount() {
        `when`(bugService.getBugByAccount(anyOrNull())).thenReturn(Flux.just(created))

        testClient.get()
                .uri("/bug/account/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$[0].accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$[0].comment").isEqualTo("test")

        verify(bugService, times(1)).getBugByAccount(anyOrNull())
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun getBugs() {
        `when`(bugService.getBugs()).thenReturn(Flux.just(created))

        testClient.get()
                .uri("/bug/all")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$[0].accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$[0].comment").isEqualTo("test")

        verify(bugService, times(1)).getBugs()
        verifyNoMoreInteractions(bugService)
    }

    @Test
    fun deleteBug() {
        `when`(bugService.deleteBug(anyOrNull())).thenReturn(Mono.just(created))

        testClient.delete()
                .uri("/bug/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
                .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
                .jsonPath("$.comment").isEqualTo("test")

        verify(bugService, times(1)).deleteBug(anyOrNull())
        verifyNoMoreInteractions(bugService)
    }
}