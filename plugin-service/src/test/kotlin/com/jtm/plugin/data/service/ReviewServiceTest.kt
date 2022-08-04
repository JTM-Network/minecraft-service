package com.jtm.plugin.data.service

import com.google.gson.GsonBuilder
import com.jtm.plugin.core.domain.dto.ReviewDto
import com.jtm.plugin.core.domain.entity.Review
import com.jtm.plugin.core.domain.exception.profile.ClientIdNotFound
import com.jtm.plugin.core.domain.exception.review.OnlyOneReview
import com.jtm.plugin.core.domain.exception.review.ReviewNotFound
import com.jtm.plugin.core.domain.model.BasicInfo
import com.jtm.plugin.core.usecase.repository.ReviewRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class ReviewServiceTest {

    private val reviewRepository: ReviewRepository = mock()
    private val reviewService = ReviewService(reviewRepository)
    private val review = Review(pluginId = UUID.randomUUID(), poster = "poster", rating = 3.5, comment = "Review comment", poster_username = "", poster_picture = "", posted = System.currentTimeMillis())
    private val reviewTwo = Review(pluginId = UUID.randomUUID(), poster = "posterTwo", rating = 5.0, comment = "Review comment Two", poster_username = "", poster_picture = "", posted = System.currentTimeMillis())
    private val dto = ReviewDto(pluginId = UUID.randomUUID(), rating = 4.5, comment = "Test comment")

    private val req: ServerHttpRequest = mock()
    private val headers: HttpHeaders = mock()
    private val gson = GsonBuilder().setPrettyPrinting().create()

    @Before
    fun setup() {
        `when`(req.headers).thenReturn(headers)
        `when`(headers.getFirst("CLIENT_ID")).thenReturn("CLIENT_ID")
        `when`(headers.getFirst("BASIC_INFO")).thenReturn(gson.toJson(BasicInfo()))
    }

    @Test
    fun addReview_thenClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = reviewService.addReview(req, dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun addReview_thenOnlyOneReview() {
        `when`(reviewRepository.findByPluginIdAndPoster(anyOrNull(), anyString())).thenReturn(Mono.just(review))

        val returned = reviewService.addReview(req, dto)

        verify(req, times(2)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(2)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(reviewRepository, times(1)).findByPluginIdAndPoster(anyOrNull(), anyString())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .expectError(OnlyOneReview::class.java)
            .verify()
    }

    @Test
    fun addReview() {
        `when`(reviewRepository.findByPluginIdAndPoster(anyOrNull(), anyString())).thenReturn(Mono.empty())
        `when`(reviewRepository.save(anyOrNull())).thenReturn(Mono.just(review))

        val returned = reviewService.addReview(req, dto)

        verify(req, times(2)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(2)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(reviewRepository, times(1)).findByPluginIdAndPoster(anyOrNull(), anyString())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(3.5)
                assertThat(it.comment).isEqualTo("Review comment")
            }
            .verifyComplete()
    }

    @Test
    fun updateRating_thenClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = reviewService.addReview(req, dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun updateRating_thenNotFound() {
        `when`(reviewRepository.findByIdAndPluginIdAndPoster(anyOrNull(), anyOrNull(), anyString())).thenReturn(Mono.empty())

        val returned = reviewService.updateRating(req, UUID.randomUUID(), dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(reviewRepository, times(1)).findByIdAndPluginIdAndPoster(anyOrNull(), anyOrNull(), anyString())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .expectError(ReviewNotFound::class.java)
            .verify()
    }

    @Test
    fun updateRating() {
        `when`(reviewRepository.findByIdAndPluginIdAndPoster(anyOrNull(), anyOrNull(), anyString())).thenReturn(Mono.just(review))
        `when`(reviewRepository.save(anyOrNull())).thenReturn(Mono.just(review))

        val returned = reviewService.updateRating(req, UUID.randomUUID(), dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(reviewRepository, times(1)).findByIdAndPluginIdAndPoster(anyOrNull(), anyOrNull(), anyString())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(4.5)
                assertThat(it.comment).isEqualTo("Review comment")
            }
            .verifyComplete()
    }

    @Test
    fun updateComment_thenClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = reviewService.updateComment(req, UUID.randomUUID(), dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun updateComment_thenNotFound() {
        `when`(reviewRepository.findByIdAndPluginIdAndPoster(anyOrNull(), anyOrNull(), anyString())).thenReturn(Mono.empty())

        val returned = reviewService.updateComment(req, UUID.randomUUID(), dto)

        verify(reviewRepository, times(1)).findByIdAndPluginIdAndPoster(anyOrNull(), anyOrNull(), anyString())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .expectError(ReviewNotFound::class.java)
            .verify()
    }

    @Test
    fun updateComment() {
        `when`(reviewRepository.findByIdAndPluginIdAndPoster(anyOrNull(), anyOrNull(), anyString())).thenReturn(Mono.just(review))
        `when`(reviewRepository.save(anyOrNull())).thenReturn(Mono.just(review))

        val returned = reviewService.updateComment(req, UUID.randomUUID(), dto)

        verify(reviewRepository, times(1)).findByIdAndPluginIdAndPoster(anyOrNull(), anyOrNull(), anyString())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(3.5)
                assertThat(it.comment).isEqualTo("Test comment")
            }
            .verifyComplete()
    }

    @Test
    fun getReview_thenNotFound() {
        `when`(reviewRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = reviewService.getReview(UUID.randomUUID())

        verify(reviewRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .expectError(ReviewNotFound::class.java)
            .verify()
    }

    @Test
    fun getReview() {
        `when`(reviewRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(review))

        val returned = reviewService.getReview(UUID.randomUUID())

        verify(reviewRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(3.5)
                assertThat(it.comment).isEqualTo("Review comment")
            }
            .verifyComplete()
    }

    @Test
    fun getReviewsByPluginId() {
        `when`(reviewRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(review, reviewTwo))

        val returned = reviewService.getReviewsByPluginId(UUID.randomUUID())

        verify(reviewRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(3.5)
                assertThat(it.comment).isEqualTo("Review comment")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(reviewTwo.id)
                assertThat(it.rating).isEqualTo(5.0)
                assertThat(it.comment).isEqualTo("Review comment Two")
            }
            .verifyComplete()
    }

    @Test
    fun getRatingByPlugin_thenEmpty() {
        `when`(reviewRepository.findByPluginId(anyOrNull())).thenReturn(Flux.empty())

        val returned = reviewService.getRatingByPlugin(UUID.randomUUID())

        verify(reviewRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it).isEqualTo(0) }
            .verifyComplete()
    }

    @Test
    fun getRatingByPlugin() {
        `when`(reviewRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(review, reviewTwo))

        val returned = reviewService.getRatingByPlugin(UUID.randomUUID())

        verify(reviewRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext { assertThat(it).isEqualTo(4) }
            .verifyComplete()
    }

    @Test
    fun getReviewsByPoster_thenClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = reviewService.getReviewsByPoster(req)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun getReviewsByPoster() {
        `when`(reviewRepository.findByPoster(anyString())).thenReturn(Flux.just(review, reviewTwo))

        val returned = reviewService.getReviewsByPoster(req)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(reviewRepository, times(1)).findByPoster(anyString())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(3.5)
                assertThat(it.comment).isEqualTo("Review comment")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(reviewTwo.id)
                assertThat(it.rating).isEqualTo(5.0)
                assertThat(it.comment).isEqualTo("Review comment Two")
            }
            .verifyComplete()
    }

    @Test
    fun getReviewsByPosterId() {
        `when`(reviewRepository.findByPoster(anyString())).thenReturn(Flux.just(review, reviewTwo))

        val returned = reviewService.getReviewsByPosterId("test")

        verify(reviewRepository, times(1)).findByPoster(anyString())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(3.5)
                assertThat(it.comment).isEqualTo("Review comment")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(reviewTwo.id)
                assertThat(it.rating).isEqualTo(5.0)
                assertThat(it.comment).isEqualTo("Review comment Two")
            }
            .verifyComplete()
    }

    @Test
    fun getReviews() {
        `when`(reviewRepository.findAll()).thenReturn(Flux.just(review, reviewTwo))

        val returned = reviewService.getReviews()

        verify(reviewRepository, times(1)).findAll()
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(3.5)
                assertThat(it.comment).isEqualTo("Review comment")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(reviewTwo.id)
                assertThat(it.rating).isEqualTo(5.0)
                assertThat(it.comment).isEqualTo("Review comment Two")
            }
            .verifyComplete()
    }

    @Test
    fun removeReview_thenNotFound() {
        `when`(reviewRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = reviewService.removeReview(UUID.randomUUID())

        verify(reviewRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .expectError(ReviewNotFound::class.java)
            .verify()
    }

    @Test
    fun removeReview() {
        `when`(reviewRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(review))
        `when`(reviewRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = reviewService.removeReview(UUID.randomUUID())

        verify(reviewRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(3.5)
                assertThat(it.comment).isEqualTo("Review comment")
            }
            .verifyComplete()
    }
}