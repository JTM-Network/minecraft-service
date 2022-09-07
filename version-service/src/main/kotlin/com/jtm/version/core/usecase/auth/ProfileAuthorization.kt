package com.jtm.version.core.usecase.auth

import com.jtm.version.core.domain.dto.AuthDto
import com.jtm.version.core.domain.exceptions.authentication.FailedProcessingRequest
import com.jtm.version.core.domain.exceptions.authentication.ProfileUnauthorized
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.util.*

@Component
class ProfileAuthorization @Autowired constructor(@Value("\${profile.host}") host: String, @Value("\${profile.port}") port: Int) {

    private val client = WebClient.create("${host}:${port}")

    fun authorize(clientId: String, plugin: UUID): Mono<Boolean> {
        return client.post()
            .uri("/authorize/plugin")
            .bodyValue(AuthDto(clientId, plugin))
            .retrieve()
            .onStatus({ code -> code.is4xxClientError }, { Mono.error(ProfileUnauthorized()) })
            .onStatus({ code -> code.is5xxServerError }, { Mono.error(FailedProcessingRequest()) })
            .bodyToMono<Void>()
            .map { true }
    }
}
