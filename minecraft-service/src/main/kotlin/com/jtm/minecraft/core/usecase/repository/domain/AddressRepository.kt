package com.jtm.minecraft.core.usecase.repository.domain

import com.jtm.minecraft.core.domain.entity.domain.Address
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository: ReactiveMongoRepository<Address, String>