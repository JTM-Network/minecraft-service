package com.jtm.minecraft.entrypoint.controller.domain

import com.jtm.minecraft.core.domain.entity.domain.Domain
import com.jtm.minecraft.data.service.domain.DomainService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
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

@RunWith(SpringRunner::class)
@WebFluxTest(DomainController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class DomainControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var domainService: DomainService

    private val domain = Domain("test")

    @Test
    fun postDomainTest() {
        `when`(domainService.insertDomain(anyOrNull())).thenReturn(Mono.just(domain))

        testClient.post()
            .uri("/domain")
            .bodyValue(Domain("test #1"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.domain").isEqualTo("test")

        verify(domainService, times(1)).insertDomain(anyOrNull())
        verifyNoMoreInteractions(domainService)
    }

    @Test
    fun getDomainsTest() {
        `when`(domainService.getDomains()).thenReturn(Flux.just(domain))

        testClient.get()
            .uri("/domain/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].domain").isEqualTo("test")

        verify(domainService, times(1)).getDomains()
        verifyNoMoreInteractions(domainService)
    }

    @Test
    fun deleteDomainTest() {
        `when`(domainService.deleteDomain(anyString())).thenReturn(Mono.just(domain))


        testClient.delete()
            .uri("/domain/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.domain").isEqualTo("test")

        verify(domainService, times(1)).deleteDomain(anyString())
        Mockito.verifyNoMoreInteractions(domainService)
    }
}