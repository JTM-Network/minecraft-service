package com.jtm.minecraft.core.usecase.repository

import com.jtm.minecraft.core.domain.entity.DownloadLink
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface DownloadLinkRepository: ReactiveMongoRepository<DownloadLink, UUID> {

    fun findByIdAndAccountId(id: UUID, accountId: UUID): Mono<DownloadLink>
}