package com.jtm.minecraft.data.service.domain

import com.jtm.minecraft.core.domain.entity.domain.Domain
import com.jtm.minecraft.core.domain.exceptions.domain.DomainFound
import com.jtm.minecraft.core.domain.exceptions.domain.DomainNotFound
import com.jtm.minecraft.core.usecase.repository.domain.DomainRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class DomainService @Autowired constructor(private val domainRepository: DomainRepository) {

    fun insertDomain(domain: Domain): Mono<Domain> {
        return domainRepository.findById(domain.domain)
            .flatMap<Domain?> { Mono.defer { Mono.error { DomainFound() } } }
            .switchIfEmpty(Mono.defer { domainRepository.save(domain) })
    }

    fun getDomains(): Flux<Domain> {
        return domainRepository.findAll()
    }

    fun deleteDomain(domain: String): Mono<Domain> {
        return domainRepository.findById(domain)
            .switchIfEmpty(Mono.defer { Mono.error(DomainNotFound()) })
            .flatMap { domainRepository.delete(it).thenReturn(it) }
    }
}