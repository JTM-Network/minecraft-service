package com.jtm.minecraft.data.service.plugin

import com.jtm.minecraft.core.domain.dto.BugDto
import com.jtm.minecraft.core.domain.entity.plugin.Bug
import com.jtm.minecraft.core.domain.exceptions.plugin.bug.BugFound
import com.jtm.minecraft.core.domain.exceptions.plugin.bug.BugNotFound
import com.jtm.minecraft.core.domain.exceptions.token.InvalidJwtToken
import com.jtm.minecraft.core.usecase.repository.plugin.BugRepository
import com.jtm.minecraft.core.usecase.token.AccountTokenProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class BugServiceTest {

    private val bugRepository: BugRepository = mock()
    private val tokenProvider: AccountTokenProvider = mock()
    private val bugService = BugService(bugRepository, tokenProvider)

    private val created = Bug(pluginId = UUID.randomUUID(), accountId = UUID.randomUUID(), comment = "test")
    private val dto = BugDto(pluginId = UUID.randomUUID(), comment = "test comment")
    private val request: ServerHttpRequest = mock()

    @Before
    fun setup() {
        val headers: HttpHeaders = mock()

        `when`(request.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("Bearer test")
        `when`(tokenProvider.resolveToken(anyString())).thenReturn("test")
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(UUID.randomUUID())
    }

    @Test
    fun addBug_thenInvalidToken() {
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = bugService.addBug(request, dto)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
                .expectError(InvalidJwtToken::class.java)
                .verify()
    }

    @Test
    fun addBug_thenFound() {
        `when`(bugRepository.findByPluginIdAndAccountId(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))

        val returned = bugService.addBug(request, dto)

        verify(bugRepository, times(1)).findByPluginIdAndAccountId(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .expectError(BugFound::class.java)
                .verify()
    }

    @Test
    fun addBug() {
        `when`(bugRepository.findByPluginIdAndAccountId(anyOrNull(), anyOrNull())).thenReturn(Mono.empty())
        `when`(bugRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = bugService.addBug(request, dto)

        verify(bugRepository, times(1)).findByPluginIdAndAccountId(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }

    @Test
    fun updateBugComment_thenInvalidToken() {
        `when`(tokenProvider.getAccountId(anyString())).thenReturn(null)

        val returned = bugService.updateBugComment(request, dto)

        verify(tokenProvider, times(1)).resolveToken(anyString())
        verify(tokenProvider, times(1)).getAccountId(anyString())
        verifyNoMoreInteractions(tokenProvider)

        StepVerifier.create(returned)
                .expectError(InvalidJwtToken::class.java)
                .verify()
    }

    @Test
    fun updateBugComment_thenNotFound() {
        `when`(bugRepository.findByPluginIdAndAccountId(anyOrNull(), anyOrNull())).thenReturn(Mono.empty())

        val returned = bugService.updateBugComment(request, dto)

        verify(bugRepository, times(1)).findByPluginIdAndAccountId(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .expectError(BugNotFound::class.java)
                .verify()
    }

    @Test
    fun updateBugComment() {
        `when`(bugRepository.findByPluginIdAndAccountId(anyOrNull(), anyOrNull())).thenReturn(Mono.just(created))
        `when`(bugRepository.save(anyOrNull())).thenReturn(Mono.just(created))

        val returned = bugService.updateBugComment(request, dto)

        verify(bugRepository, times(1)).findByPluginIdAndAccountId(anyOrNull(), anyOrNull())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test comment")
                }
                .verifyComplete()
    }

    @Test
    fun getBug_thenNotFound() {
        `when`(bugRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = bugService.getBug(UUID.randomUUID())

        verify(bugRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .expectError(BugNotFound::class.java)
                .verify()
    }

    @Test
    fun getBug() {
        `when`(bugRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))

        val returned = bugService.getBug(UUID.randomUUID())

        verify(bugRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }

    @Test
    fun getBugsByPlugin() {
        `when`(bugRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(created))

        val returned = bugService.getBugByPlugin(UUID.randomUUID())

        verify(bugRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }

    @Test
    fun getBugsByAccount() {
        `when`(bugRepository.findByAccountId(anyOrNull())).thenReturn(Flux.just(created))

        val returned = bugService.getBugByAccount(UUID.randomUUID())

        verify(bugRepository, times(1)).findByAccountId(anyOrNull())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }

    @Test
    fun getBugs() {
        `when`(bugRepository.findAll()).thenReturn(Flux.just(created))

        val returned = bugService.getBugs()

        verify(bugRepository, times(1)).findAll()
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }

    @Test
    fun deleteBug_thenNotFound() {
        `when`(bugRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = bugService.deleteBug(UUID.randomUUID())

        verify(bugRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .expectError(BugNotFound::class.java)
                .verify()
    }

    @Test
    fun deleteBug() {
        `when`(bugRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(created))
        `when`(bugRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = bugService.deleteBug(UUID.randomUUID())

        verify(bugRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
                .assertNext {
                    assertThat(it.pluginId).isEqualTo(created.pluginId)
                    assertThat(it.accountId).isEqualTo(created.accountId)
                    assertThat(it.comment).isEqualTo("test")
                }
                .verifyComplete()
    }
}