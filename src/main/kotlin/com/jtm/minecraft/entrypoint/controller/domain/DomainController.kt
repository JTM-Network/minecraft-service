package com.jtm.minecraft.entrypoint.controller.domain

import com.jtm.minecraft.core.domain.entity.domain.Domain
import com.jtm.minecraft.data.service.domain.DomainService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/domain")
class DomainController @Autowired constructor(private val domainService: DomainService) {

    @PostMapping
    fun postDomain(@RequestBody domain: Domain): Mono<Domain> {
        return domainService.insertDomain(domain)
    }

    @GetMapping("/all")
    fun getDomains(): Flux<Domain> {
        return domainService.getDomains()
    }

    @DeleteMapping("/{domain}")
    fun deleteDomain(@PathVariable domain: String): Mono<Domain> {
        return domainService.deleteDomain(domain)
    }
}