package com.jtm.version.core.usecase.auth

import com.jtm.version.core.domain.dto.PermissionDto
import com.jtm.version.core.domain.exceptions.authentication.FailedProcessingRequest
import com.jtm.version.core.domain.exceptions.authentication.ProfileUnauthorized
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*

@Component
class ProfileAuthorization @Autowired constructor(@Value("\${profile.host}") host: String) {

    private val logger = LoggerFactory.getLogger(ProfileAuthorization::class.java)
    private val baseUrl = host

    private val client = WebClient.create()

    fun authorize(clientId: String, plugin: UUID): Mono<Boolean> {
        return client.post()
            .uri("$baseUrl/plugin/access/auth")
            .bodyValue(PermissionDto(clientId, plugin.toString()))
            .exchangeToMono {
                if (it.statusCode().is5xxServerError) {
                    logger.error("Failed processing request.")
                    return@exchangeToMono Mono.error(FailedProcessingRequest())
                }

                if (it.statusCode().is4xxClientError) {
                    logger.error("Profile unauthorized: ${it.statusCode().value()}")
                    return@exchangeToMono Mono.error(ProfileUnauthorized())
                }

                return@exchangeToMono Mono.just(true)
            }
    }
}
