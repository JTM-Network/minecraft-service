package com.jtm.minecraft.data.service.domain

import com.jtm.minecraft.core.domain.entity.domain.Address
import com.jtm.minecraft.core.domain.entity.domain.Domain
import com.jtm.minecraft.core.domain.exceptions.domain.AddressUnauthorized
import com.jtm.minecraft.core.domain.exceptions.domain.DomainUnauthorized
import com.jtm.minecraft.core.domain.exceptions.RemoteAddressInvalid
import com.jtm.minecraft.core.usecase.repository.domain.AddressRepository
import com.jtm.minecraft.core.usecase.repository.domain.DomainRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.net.InetSocketAddress

@RunWith(SpringRunner::class)
class DomainIpServiceTest {

    private val addressRepository: AddressRepository = mock()
    private val domainRepository: DomainRepository = mock()
    private val domainIpService = DomainIpService(addressRepository, domainRepository)

    private val request: ServerHttpRequest = mock()
    private val remoteAddress = InetSocketAddress("localhost", 3306)

    @Test
    fun authenticate_thenRemoteAddressInvalid() {
        `when`(request.remoteAddress).thenReturn(null)

        val returned = domainIpService.authenticate(request)

        verify(request, times(1)).remoteAddress
        verifyNoMoreInteractions(request)

        StepVerifier.create(returned)
            .expectError(RemoteAddressInvalid::class.java)
            .verify()
    }

    @Test
    fun authenticate_thenAddressUnauthorized() {
        `when`(request.remoteAddress).thenReturn(remoteAddress)
        `when`(addressRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = domainIpService.authenticate(request)

        verify(request, times(1)).remoteAddress
        verifyNoMoreInteractions(request)

        verify(addressRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(addressRepository)

        StepVerifier.create(returned)
            .expectError(AddressUnauthorized::class.java)
            .verify()
    }

    @Test
    fun authenticate_thenDomainUnauthorized() {
        `when`(request.remoteAddress).thenReturn(remoteAddress)
        `when`(addressRepository.findById(anyString())).thenReturn(Mono.just(Address("test")))
        `when`(domainRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = domainIpService.authenticate(request)

        verify(request, times(1)).remoteAddress
        verifyNoMoreInteractions(request)

        verify(addressRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(addressRepository)

        StepVerifier.create(returned)
            .expectError(DomainUnauthorized::class.java)
            .verify()
    }

    @Test
    fun authenticateTest() {
        `when`(request.remoteAddress).thenReturn(remoteAddress)
        `when`(addressRepository.findById(anyString())).thenReturn(Mono.just(Address("test")))
        `when`(domainRepository.findById(anyString())).thenReturn(Mono.just(Domain("domain")))

        val returned = domainIpService.authenticate(request)

        verify(request, times(1)).remoteAddress
        verifyNoMoreInteractions(request)

        verify(addressRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(addressRepository)

        StepVerifier.create(returned)
            .verifyComplete()
    }
}