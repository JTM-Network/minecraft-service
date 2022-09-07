package com.jtm.plugin.core.usecase.provider

import com.jtm.plugin.core.domain.dto.PermissionDto
import com.jtm.plugin.core.domain.exception.profile.FailedCheckingAccess
import com.jtm.plugin.core.domain.exception.profile.FailedGivingAccess
import com.jtm.plugin.core.domain.exception.profile.NoAccess
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*


class AccessProvider @Autowired constructor(@Value("\${profile.host}") host: String, @Value("\${profile.port}") port: Int) {

    private val client = WebClient.create("${host}:${port}")
    private val logger = LoggerFactory.getLogger(AccessProvider::class.java)

    fun addAccess(id: String, pluginId: UUID): Mono<Void> {
        return client.post()
            .uri("/plugin/access")
            .bodyValue(PermissionDto(id, pluginId.toString()))
            .exchangeToMono {
                if (it.statusCode().is5xxServerError) {
                    logger.error("Failed to give access to user: ${it.statusCode().value()}")
                    return@exchangeToMono Mono.error(FailedGivingAccess())
                }

                if (it.statusCode().is4xxClientError) {
                    logger.error("No access for user: ${it.statusCode().value()}")
                    return@exchangeToMono Mono.error(NoAccess())
                }

                return@exchangeToMono Mono.empty()
            }
    }

    fun checkAccess(id: String, pluginId: UUID): Mono<Void> {
        return client.get()
            .uri("/plugin/access/auth?id='$id'&permission=$pluginId")
            .exchangeToMono {
                if (it.statusCode().is5xxServerError) {
                    logger.error("Failed to check access for user.")
                    return@exchangeToMono Mono.error(FailedCheckingAccess())
                }

                if (it.statusCode().is4xxClientError) {
                    logger.error("No access for user: ${it.statusCode().value()}")
                    return@exchangeToMono Mono.error(NoAccess())
                }

                return@exchangeToMono Mono.empty()
            }
    }
}