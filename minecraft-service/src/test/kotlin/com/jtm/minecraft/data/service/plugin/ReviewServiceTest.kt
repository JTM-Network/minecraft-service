package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.PluginReviewDto
import com.jtm.minecraft.core.domain.entity.Plugin
import com.jtm.minecraft.core.domain.entity.plugin.PluginReview
import com.jtm.minecraft.core.domain.exceptions.plugin.PluginNotFound
import com.jtm.minecraft.core.domain.exceptions.plugin.review.ReviewFound
import com.jtm.minecraft.core.domain.exceptions.plugin.review.ReviewNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.repository.plugin.PluginReviewRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import com.jtm.minecraft.data.service.PluginService
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

    private val pluginService: PluginService = mock()
    private val reviewRepository: PluginReviewRepository = mock()
    private val tokenProvider: AccountTokenProvider = mock()
    private val reviewService = ReviewService(pluginService, reviewRepository, tokenProvider)
    private val plugin = Plugin(name = "test", description = "desc")
    private val review = PluginReview(accountId = UUID.randomUUID(), pluginId = UUID.randomUUID(), rating = 3.5, comment = "Review comment")
    private val dto = PluginReviewDto(UUID.randomUUID(), 4.5, "Review comment #1")
    private val request: ServerHttpRequest = mock()

    @Before
    fun setup() {
        val headers: HttpHeaders = mock()

        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("Bearer test")
    }

    @Test
    fun insertReview_thenAccoundIdInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = reviewService.insertReview(dto, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun insertReview_thenFound() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(reviewRepository.findByAccountIdAndPluginId(anyOrNull(), anyOrNull())).thenReturn(Mono.just(review))

        val returned = reviewService.insertReview(dto, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .expectError(ReviewFound::class.java)
            .verify()
    }

    @Test
    fun insertReviewTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(reviewRepository.findByAccountIdAndPluginId(anyOrNull(), anyOrNull())).thenReturn(Mono.empty())
        `when`(reviewRepository.save(anyOrNull())).thenReturn(Mono.just(review))

        val returned = reviewService.insertReview(dto, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(review.rating)
                assertThat(it.comment).isEqualTo(review.comment)
            }
            .verifyComplete()
    }

    @Test
    fun updateReviewRating_thenAccountIdInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = reviewService.updateReviewRating(dto, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun updateReviewRating_thenNotFound() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(reviewRepository.findByAccountIdAndPluginId(anyOrNull(), anyOrNull())).thenReturn(Mono.empty())

        val returned = reviewService.updateReviewRating(dto, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .expectError(ReviewNotFound::class.java)
            .verify()
    }

    @Test
    fun updateReviewRatingTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(reviewRepository.findByAccountIdAndPluginId(anyOrNull(), anyOrNull())).thenReturn(Mono.just(review))
        `when`(reviewRepository.save(anyOrNull())).thenReturn(Mono.just(review))

        val returned = reviewService.updateReviewRating(dto, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(review.rating)
                assertThat(it.comment).isEqualTo(review.comment)
            }
            .verifyComplete()
    }

    @Test
    fun updateReviewComment_thenAccountIdInvalid() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = reviewService.updateReviewComment(dto, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
            .expectError(InvalidJwtToken::class.java)
            .verify()
    }

    @Test
    fun updateReviewComment_thenNotFound() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(reviewRepository.findByAccountIdAndPluginId(anyOrNull(), anyOrNull())).thenReturn(Mono.empty())

        val returned = reviewService.updateReviewComment(dto, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .expectError(ReviewNotFound::class.java)
            .verify()
    }

    @Test
    fun updateReviewCommentTest() {
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
        `when`(pluginService.getPlugin(anyOrNull())).thenReturn(Mono.just(plugin))
        `when`(reviewRepository.findByAccountIdAndPluginId(anyOrNull(), anyOrNull())).thenReturn(Mono.just(review))
        `when`(reviewRepository.save(anyOrNull())).thenReturn(Mono.just(review))

        val returned = reviewService.updateReviewComment(dto, request)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        verify(pluginService, times(1)).getPlugin(anyOrNull())
        verifyNoMoreInteractions(pluginService)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(review.rating)
                assertThat(it.comment).isEqualTo(review.comment)
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
    fun getReviewTest() {
        `when`(reviewRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(review))

        val returned = reviewService.getReview(UUID.randomUUID())

        verify(reviewRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(review.rating)
                assertThat(it.comment).isEqualTo(review.comment)
            }
            .verifyComplete()
    }

    @Test
    fun getReviewByPluginTest() {
        `when`(reviewRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(review))

        val returned = reviewService.getReviewByPlugin(UUID.randomUUID())

        verify(reviewRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(review.rating)
                assertThat(it.comment).isEqualTo(review.comment)
            }
            .verifyComplete()
    }

    @Test
    fun getReviewsTest() {
        `when`(reviewRepository.findAll()).thenReturn(Flux.just(review))

        val returned = reviewService.getReviews()

        verify(reviewRepository, times(1)).findAll()
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(review.rating)
                assertThat(it.comment).isEqualTo(review.comment)
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
    fun removeReviewTest() {
        `when`(reviewRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(review))
        `when`(reviewRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = reviewService.removeReview(UUID.randomUUID())

        verify(reviewRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(reviewRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(review.id)
                assertThat(it.rating).isEqualTo(review.rating)
                assertThat(it.comment).isEqualTo(review.comment)
            }
            .verifyComplete()
    }
}