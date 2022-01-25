package com.jtm.minecraft.entrypoint.controller.domain

import com.jtm.minecraft.core.domain.entity.domain.Address
import com.jtm.minecraft.data.service.domain.AddressService
import org.junit.Test
import org.junit.runner.RunWith
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
@WebFluxTest(AddressController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class AddressControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var addressService: AddressService
    private val address = Address("test")

    @Test
    fun postAddressTest() {
        `when`(addressService.insertAddress(anyOrNull())).thenReturn(Mono.just(address))

        testClient.post()
            .uri("/address")
            .bodyValue(address)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.address").isEqualTo("test")

        verify(addressService, times(1)).insertAddress(anyOrNull())
        verifyNoMoreInteractions(addressService)
    }

    @Test
    fun getAddressesTest() {
        `when`(addressService.getAddresses()).thenReturn(Flux.just(address))

        testClient.get()
            .uri("/address/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].address")

        verify(addressService, times(1)).getAddresses()
        verifyNoMoreInteractions(addressService)
    }

    @Test
    fun deleteAddressTest() {
        `when`(addressService.deleteAddress(anyString())).thenReturn(Mono.just(address))

        testClient.delete()
            .uri("/address/test")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.address").isEqualTo("test")

        verify(addressService, times(1)).deleteAddress(anyString())
        verifyNoMoreInteractions(addressService)
    }
}