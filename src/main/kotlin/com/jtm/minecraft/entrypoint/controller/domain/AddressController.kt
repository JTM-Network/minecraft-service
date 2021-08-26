package com.jtm.minecraft.entrypoint.controller.domain

import com.jtm.minecraft.core.domain.entity.domain.Address
import com.jtm.minecraft.data.service.domain.AddressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/address")
class AddressController @Autowired constructor(private val addressService: AddressService) {

    @PostMapping
    fun postAddress(@RequestBody address: Address): Mono<Address> {
        return addressService.insertAddress(address)
    }

    @GetMapping("/all")
    fun getAddresses(): Flux<Address> {
        return addressService.getAddresses()
    }

    @DeleteMapping("/{address}")
    fun deleteAddress(@PathVariable address: String): Mono<Address> {
        return addressService.deleteAddress(address)
    }
}