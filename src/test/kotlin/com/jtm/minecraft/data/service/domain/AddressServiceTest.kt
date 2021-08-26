package com.jtm.minecraft.data.service.domain

import com.jtm.minecraft.core.domain.entity.domain.Address
import com.jtm.minecraft.core.domain.exceptions.domain.AddressFound
import com.jtm.minecraft.core.domain.exceptions.domain.AddressNotFound
import com.jtm.minecraft.core.usecase.repository.domain.AddressRepository
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
class AddressServiceTest {

    private val addressRepository: AddressRepository = mock()
    private val addressService = AddressService(addressRepository)
    private val address = Address("test")

    @Test
    fun insertAddress_thenFound() {
        `when`(addressRepository.findById(anyString())).thenReturn(Mono.just(address))

        val returned = addressService.insertAddress(address)

        verify(addressRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(addressRepository)

        StepVerifier.create(returned)
            .expectError(AddressFound::class.java)
            .verify()
    }

    @Test
    fun insertAddressTest() {
        `when`(addressRepository.findById(anyString())).thenReturn(Mono.empty())
        `when`(addressRepository.save(anyOrNull())).thenReturn(Mono.just(address))

        val returned = addressService.insertAddress(address)

        verify(addressRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(addressRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.address).isEqualTo("test") }
            .verifyComplete()
    }

    @Test
    fun getAddressesTest() {
        `when`(addressRepository.findAll()).thenReturn(Flux.just(address))

        val returned = addressService.getAddresses()

        verify(addressRepository, times(1)).findAll()
        verifyNoMoreInteractions(addressRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.address).isEqualTo("test") }
            .verifyComplete()
    }

    @Test
    fun deleteAddress_thenNotFound() {
        `when`(addressRepository.findById(anyString())).thenReturn(Mono.empty())

        val returned = addressService.deleteAddress("test")

        verify(addressRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(addressRepository)

        StepVerifier.create(returned)
            .expectError(AddressNotFound::class.java)
            .verify()
    }

    @Test
    fun deleteAddressTest() {
        `when`(addressRepository.findById(anyString())).thenReturn(Mono.just(address))
        `when`(addressRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = addressService.deleteAddress("test")

        verify(addressRepository, times(1)).findById(anyString())
        verifyNoMoreInteractions(addressRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it.address).isEqualTo("test") }
            .verifyComplete()
    }
}