package com.jtm.plugin.entrypoint.controller

import com.jtm.plugin.core.domain.dto.ReviewDto
import com.jtm.plugin.core.domain.entity.Review
import com.jtm.plugin.data.service.ReviewService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
@WebFluxTest(ReviewController::class)
@AutoConfigureWebTestClient
class ReviewControllerTest {

    @Autowired
    lateinit var testClient: WebTestClient

    @MockBean
    lateinit var reviewService: ReviewService

    private val review = Review(pluginId = UUID.randomUUID(), poster = "poster", rating = 3.5, comment = "Review comment", poster_username = "", poster_picture = "", posted = System.currentTimeMillis())
    private val reviewTwo = Review(pluginId = UUID.randomUUID(), poster = "posterTwo", rating = 5.0, comment = "Review comment Two", poster_username = "", poster_picture = "", posted = System.currentTimeMillis())
    private val dto = ReviewDto(pluginId = UUID.randomUUID(), rating = 4.5, comment = "Test comment")

    @Test
    fun postReview() {
        `when`(reviewService.addReview(anyOrNull(), anyOrNull())).thenReturn(Mono.just(review))

        testClient.post()
            .uri("/review")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(review.pluginId.toString())
            .jsonPath("$.poster").isEqualTo("poster")
            .jsonPath("$.rating").isEqualTo(3.5)
            .jsonPath("$.comment").isEqualTo("Review comment")

        verify(reviewService, times(1)).addReview(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun putRating() {
        `when`(reviewService.updateRating(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(Mono.just(review))

        testClient.put()
            .uri("/review/${UUID.randomUUID()}/rating")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(review.pluginId.toString())
            .jsonPath("$.poster").isEqualTo("poster")
            .jsonPath("$.rating").isEqualTo(3.5)
            .jsonPath("$.comment").isEqualTo("Review comment")

        verify(reviewService, times(1)).updateRating(anyOrNull(), anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun putComment() {
        `when`(reviewService.updateComment(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(Mono.just(review))

        testClient.put()
            .uri("/review/${UUID.randomUUID()}/comment")
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(review.pluginId.toString())
            .jsonPath("$.poster").isEqualTo("poster")
            .jsonPath("$.rating").isEqualTo(3.5)
            .jsonPath("$.comment").isEqualTo("Review comment")

        verify(reviewService, times(1)).updateComment(anyOrNull(), anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun getReview() {
        `when`(reviewService.getReview(anyOrNull())).thenReturn(Mono.just(review))

        testClient.get()
            .uri("/review/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(review.pluginId.toString())
            .jsonPath("$.poster").isEqualTo("poster")
            .jsonPath("$.rating").isEqualTo(3.5)
            .jsonPath("$.comment").isEqualTo("Review comment")

        verify(reviewService, times(1)).getReview(anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun getReviewsByPlugin() {
        `when`(reviewService.getReviewsByPluginId(anyOrNull())).thenReturn(Flux.just(review, reviewTwo))

        testClient.get()
            .uri("/review/plugin/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(review.pluginId.toString())
            .jsonPath("$[0].poster").isEqualTo("poster")
            .jsonPath("$[0].rating").isEqualTo(3.5)
            .jsonPath("$[0].comment").isEqualTo("Review comment")

            .jsonPath("$[1].pluginId").isEqualTo(reviewTwo.pluginId.toString())
            .jsonPath("$[1].poster").isEqualTo("posterTwo")
            .jsonPath("$[1].rating").isEqualTo(5.0)
            .jsonPath("$[1].comment").isEqualTo("Review comment Two")

        verify(reviewService, times(1)).getReviewsByPluginId(anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun getRatingByPlugin() {
        `when`(reviewService.getRatingByPlugin(anyOrNull())).thenReturn(Mono.just(4))

        testClient.get()
            .uri("/review/rating/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody(Int::class.java)
            .isEqualTo(4)

        verify(reviewService, times(1)).getRatingByPlugin(anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun getReviewsByPoster() {
        `when`(reviewService.getReviewsByPoster(anyOrNull())).thenReturn(Flux.just(review, reviewTwo))

        testClient.get()
            .uri("/review/poster")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(review.pluginId.toString())
            .jsonPath("$[0].poster").isEqualTo("poster")
            .jsonPath("$[0].rating").isEqualTo(3.5)
            .jsonPath("$[0].comment").isEqualTo("Review comment")

            .jsonPath("$[1].pluginId").isEqualTo(reviewTwo.pluginId.toString())
            .jsonPath("$[1].poster").isEqualTo("posterTwo")
            .jsonPath("$[1].rating").isEqualTo(5.0)
            .jsonPath("$[1].comment").isEqualTo("Review comment Two")

        verify(reviewService, times(1)).getReviewsByPoster(anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun getReviewsByPosterId() {
        `when`(reviewService.getReviewsByPosterId(anyOrNull())).thenReturn(Flux.just(review, reviewTwo))

        testClient.get()
            .uri("/review/poster/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(review.pluginId.toString())
            .jsonPath("$[0].poster").isEqualTo("poster")
            .jsonPath("$[0].rating").isEqualTo(3.5)
            .jsonPath("$[0].comment").isEqualTo("Review comment")

            .jsonPath("$[1].pluginId").isEqualTo(reviewTwo.pluginId.toString())
            .jsonPath("$[1].poster").isEqualTo("posterTwo")
            .jsonPath("$[1].rating").isEqualTo(5.0)
            .jsonPath("$[1].comment").isEqualTo("Review comment Two")

        verify(reviewService, times(1)).getReviewsByPosterId(anyString())
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun getReviews() {
        `when`(reviewService.getReviews()).thenReturn(Flux.just(review, reviewTwo))

        testClient.get()
            .uri("/review/all")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].pluginId").isEqualTo(review.pluginId.toString())
            .jsonPath("$[0].poster").isEqualTo("poster")
            .jsonPath("$[0].rating").isEqualTo(3.5)
            .jsonPath("$[0].comment").isEqualTo("Review comment")

            .jsonPath("$[1].pluginId").isEqualTo(reviewTwo.pluginId.toString())
            .jsonPath("$[1].poster").isEqualTo("posterTwo")
            .jsonPath("$[1].rating").isEqualTo(5.0)
            .jsonPath("$[1].comment").isEqualTo("Review comment Two")

        verify(reviewService, times(1)).getReviews()
        verifyNoMoreInteractions(reviewService)
    }

    @Test
    fun deleteReview() {
        `when`(reviewService.removeReview(anyOrNull())).thenReturn(Mono.just(review))

        testClient.delete()
            .uri("/review/${UUID.randomUUID()}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.pluginId").isEqualTo(review.pluginId.toString())
            .jsonPath("$.poster").isEqualTo("poster")
            .jsonPath("$.rating").isEqualTo(3.5)
            .jsonPath("$.comment").isEqualTo("Review comment")

        verify(reviewService, times(1)).removeReview(anyOrNull())
        verifyNoMoreInteractions(reviewService)
    }
}