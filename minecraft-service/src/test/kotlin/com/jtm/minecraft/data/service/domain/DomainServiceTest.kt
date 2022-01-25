package com.jtm.minecraft.data.service.domain

import com.jtm.minecraft.core.domain.entity.domain.Domain
import com.jtm.minecraft.core.domain.exceptions.domain.DomainFound
import com.jtm.minecraft.core.domain.exceptions.domain.DomainNotFound
import com.jtm.minecraft.core.usecase.repository.domain.DomainRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@RunWith(SpringRunner::class)
class DomainServiceTest {

    private val domainRepository: DomainRepository = mock()
    private val domainService = DomainService(domainRepository)
    private val domain = Domain("test")

    @Test
    fun insertDomain_thenFound() {
        `when`(domainRepository.findById(anyString())).thenReturn(Mono.just(domain))

        val returned = domainService.insertDomain(domain)

        verify(domainRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(domainRepository)

        StepVerifier.create(returned)
            .expectError(DomainFound::class.java)
            .verify()
    }

    @Test
    fun insertDomainTest() {
        `when`(domainRepository.findById(anyString())).thenReturn(Mono.empty())
        `when`(domainRepository.save(anyOrNull())).thenReturn(Mono.just(domain))

        val returned = domainService.insertDomain(Domain("test #1"))

        verify(domainRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(domainRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.domain).isEqualTo("test") }
            .verifyComplete()
    }

    @Test
    fun getDomainsTest() {
        `when`(domainRepository.findAll()).thenReturn(Flux.just(domain))

        val returned = domainService.getDomains()

        verify(domainRepository, times(1)).findAll()
        verifyNoMoreInteractions(domainRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.domain).isEqualTo("test") }
            .verifyComplete()
    }

    @Test
    fun deleteDomain_thenNotFound() {
        `when`(domainRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = domainService.deleteDomain("test")

        verify(domainRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(domainRepository)

        StepVerifier.create(returned)
            .expectError(DomainNotFound::class.java)
            .verify()
    }

    @Test
    fun deleteDomainTest() {
        `when`(domainRepository.findById(anyString())).thenReturn(Mono.just(domain))
        `when`(domainRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = domainService.deleteDomain("test")

        verify(domainRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(domainRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.domain).isEqualTo("test") }
            .verifyComplete()
    }
}