package com.jtm.minecraft.core.usecase.repository

import com.jtm.minecraft.core.domain.entity.BlacklistToken
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BlacklistTokenRepository: ReactiveMongoRepository<BlacklistToken, String>