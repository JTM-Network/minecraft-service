package com.jtm.minecraft.data.service.domain

import com.jtm.minecraft.core.domain.exceptions.domain.AddressUnauthorized
import com.jtm.minecraft.core.domain.exceptions.domain.DomainUnauthorized
import com.jtm.minecraft.core.domain.exceptions.RemoteAddressInvalid
import com.jtm.minecraft.core.usecase.repository.domain.AddressRepository
import com.jtm.minecraft.core.usecase.repository.domain.DomainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DomainIpService @Autowired constructor(private val addressRepository: AddressRepository, private val domainRepository: DomainRepository) {

    fun authenticate(request: ServerHttpRequest): Mono<Void> {
        val remote = request.remoteAddress ?: return Mono.error { RemoteAddressInvalid() }
        val address = remote.address.hostAddress
        val domain = remote.hostName
        return addressRepository.findById(address)
            .switchIfEmpty(Mono.defer { Mono.error(AddressUnauthorized()) })
            .flatMap { domainRepository.findById(domain)
                .switchIfEmpty(Mono.defer { Mono.error(DomainUnauthorized()) })
                .flatMap { Mono.empty() }
            }
    }
}