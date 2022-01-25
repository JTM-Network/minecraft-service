package com.jtm.minecraft.entrypoint.controller.plugin

import com.jtm.minecraft.core.domain.dto.PluginReviewDto
import com.jtm.minecraft.core.domain.entity.plugin.PluginReview
import com.jtm.minecraft.data.service.plugin.ReviewService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(ReviewController::class, excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class])
@AutoConfigureWebTestClient
class ReviewControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var reviewService: ReviewService

    private val dto = PluginReviewDto(UUID.randomUUID(), 3.5, "Review comment")
    private val created = PluginReview(accountId = UUID.randomUUID(), pluginId = UUID.randomUUID(), rating = 3.5, comment = "Review comment.")

    @Test
    fun postReviewTest() {
        `when`(reviewService.insertReview(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.post()
            .uri("/review")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
            .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$.rating").isEqualTo(created.rating)
            .jsonPath("$.comment").isEqualTo(created.comment)

        verify(reviewService, times(1)).insertReview(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun putReviewRatingTest() {
        `when`(reviewService.updateReviewRating(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
            .uri("/review/rating")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
            .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$.rating").isEqualTo(created.rating)
            .jsonPath("$.comment").isEqualTo(created.comment)

        verify(reviewService, times(1)).updateReviewRating(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun putReviewCommentTest() {
        `when`(reviewService.updateReviewComment(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        testClient.put()
            .uri("/review/comment")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
            .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$.rating").isEqualTo(created.rating)
            .jsonPath("$.comment").isEqualTo(created.comment)

        verify(reviewService, times(1)).updateReviewComment(anyOrNull(), anyOrNull())
        Mockito.verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun getReviewTest() {
        `when`(reviewService.getReview(anyOrNull())).thenReturn(Mono.just(created))

        testClient.get()
            .uri("/review/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
            .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$.rating").isEqualTo(created.rating)
            .jsonPath("$.comment").isEqualTo(created.comment)

        verify(reviewService, times(1)).getReview(anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun getReviewByPluginTest() {
        `when`(reviewService.getReviewByPlugin(anyOrNull())).thenReturn(Flux.just(created))

        testClient.get()
            .uri("/review/plugin/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].accountId").isEqualTo(created.accountId.toString())
            .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$[0].rating").isEqualTo(created.rating)
            .jsonPath("$[0].comment").isEqualTo(created.comment)

        verify(reviewService, times(1)).getReviewByPlugin(anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun getReviewsTest() {
        `when`(reviewService.getReviews()).thenReturn(Flux.just(created))

        testClient.get()
            .uri("/review/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].accountId").isEqualTo(created.accountId.toString())
            .jsonPath("$[0].pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$[0].rating").isEqualTo(created.rating)
            .jsonPath("$[0].comment").isEqualTo(created.comment)

        verify(reviewService, times(1)).getReviews()
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun deleteReviewTest() {
        `when`(reviewService.removeReview(anyOrNull())).thenReturn(Mono.just(created))

        testClient.delete()
            .uri("/review/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.accountId").isEqualTo(created.accountId.toString())
            .jsonPath("$.pluginId").isEqualTo(created.pluginId.toString())
            .jsonPath("$.rating").isEqualTo(created.rating)
            .jsonPath("$.comment").isEqualTo(created.comment)

        verify(reviewService, times(1)).removeReview(anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }
}