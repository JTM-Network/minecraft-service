package com.jtm.plugin.core.usecase.repository

import com.jtm.plugin.core.domain.entity.Wiki
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WikiRepository: ReactiveMongoRepository<Wiki, UUID>