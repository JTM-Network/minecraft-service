package com.jtm.minecraft.data.service.domain

import com.jtm.minecraft.core.domain.entity.domain.Address
import com.jtm.minecraft.core.domain.exceptions.domain.AddressFound
import com.jtm.minecraft.core.domain.exceptions.domain.AddressNotFound
import com.jtm.minecraft.core.usecase.repository.domain.AddressRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class AddressService @Autowired constructor(private val addressRepository: AddressRepository) {

    fun insertAddress(address: Address): Mono<Address> {
        return addressRepository.findById(address.address)
            .flatMap<Address?> { Mono.defer { Mono.error(AddressFound()) } }
            .switchIfEmpty(Mono.defer { addressRepository.save(address) })
    }

    fun getAddresses(): Flux<Address> {
        return addressRepository.findAll()
    }

    fun deleteAddress(address: String): Mono<Address> {
        return addressRepository.findById(address)
            .switchIfEmpty(Mono.defer { Mono.error(AddressNotFound()) })
            .flatMap { addressRepository.delete(it).thenReturn(it) }
    }
}