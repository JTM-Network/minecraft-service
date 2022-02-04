package com.jtm.version.core.usecase.repository

import com.jtm.version.core.domain.entity.DownloadLink
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DownloadRepository: ReactiveMongoRepository<DownloadLink, UUID>