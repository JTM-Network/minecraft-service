package com.jtm.minecraft.core.usecase.repository.domain

import com.jtm.minecraft.core.domain.entity.domain.Domain
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DomainRepository: ReactiveMongoRepository<Domain, String>