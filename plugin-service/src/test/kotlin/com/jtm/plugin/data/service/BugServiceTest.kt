package com.jtm.plugin.data.service

import com.jtm.plugin.core.domain.dto.BugDto
import com.jtm.plugin.core.domain.entity.Bug
import com.jtm.plugin.core.domain.exception.bug.BugNotFound
import com.jtm.plugin.core.domain.exception.profile.ClientIdNotFound
import com.jtm.plugin.core.domain.exception.profile.NotAllowedToPost
import com.jtm.plugin.core.usecase.repository.BugRepository
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
class BugServiceTest {

    private val bugRepository: BugRepository = mock()
    private val bugService = BugService(bugRepository)
    private val bug = Bug(pluginId = UUID.randomUUID(), poster = "poster", serverVersion = "1.16", pluginVersion = "0.1", recreateComment = "How to recreate", happensComment = "What happens.")
    private val bugTwo = Bug(pluginId = UUID.randomUUID(), poster = "posterTwo", serverVersion = "1.17", pluginVersion = "0.5", recreateComment = "How to recreate #2", happensComment = "What happens. #2")
    private val dto = BugDto(pluginId =  UUID.randomUUID(), serverVersion = "1.16", pluginVersion = "0.1", recreateComment = "How to recreate", happensComment = "What happens.")
    private val mockBug: Bug = mock()

    private val req: ServerHttpRequest = mock()
    private val headers: HttpHeaders = mock()

    @Before
    fun setup() {
        `when`(req.headers).thenReturn(headers)
        `when`(headers.getFirst(anyString())).thenReturn("CLIENT_ID")
    }

    @Test
    fun addBug_shouldThrowClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = bugService.addBug(req, dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun addBug_shouldThrowNotAllowedToPost() {
        `when`(bugRepository.findByPluginIdAndPoster(anyOrNull(), anyString())).thenReturn(Flux.just(bug, bugTwo))

        val returned = bugService.addBug(req, dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(bugRepository, times(1)).findByPluginIdAndPoster(anyOrNull(), anyString())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
            .expectError(NotAllowedToPost::class.java)
            .verify()
    }

    @Test
    fun addBug_shouldReturnCreatedBug() {
        `when`(mockBug.posted).thenReturn(System.currentTimeMillis())
        `when`(mockBug.canPost()).thenReturn(true)
        `when`(bugRepository.findByPluginIdAndPoster(anyOrNull(), anyString())).thenReturn(Flux.just(mockBug))
        `when`(bugRepository.save(anyOrNull())).thenReturn(Mono.just(bug))

        val returned = bugService.addBug(req, dto)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(bugRepository, times(1)).findByPluginIdAndPoster(anyOrNull(), anyString())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(bug.id)
                assertThat(it.pluginId).isEqualTo(bug.pluginId)
                assertThat(it.poster).isEqualTo("poster")
                assertThat(it.serverVersion).isEqualTo("1.16")
            }
            .verifyComplete()
    }

    @Test
    fun getBug_shouldThrowNotFound() {
        `when`(bugRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = bugService.getBug(UUID.randomUUID())

        verify(bugRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
            .expectError(BugNotFound::class.java)
            .verify()
    }

    @Test
    fun getBug_shouldReturnBug() {
        `when`(bugRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(bug))

        val returned = bugService.getBug(UUID.randomUUID())

        verify(bugRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(bug.id)
                assertThat(it.poster).isEqualTo("poster")
                assertThat(it.pluginId).isEqualTo(bug.pluginId)
                assertThat(it.serverVersion).isEqualTo("1.16")
            }
            .verifyComplete()
    }

    @Test
    fun getBugsByPluginId_shouldReturnBugs() {
        `when`(bugRepository.findByPluginId(anyOrNull())).thenReturn(Flux.just(bug, bugTwo))

        val returned = bugService.getBugsByPluginId(UUID.randomUUID())

        verify(bugRepository, times(1)).findByPluginId(anyOrNull())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(bug.id)
                assertThat(it.poster).isEqualTo("poster")
                assertThat(it.pluginId).isEqualTo(bug.pluginId)
                assertThat(it.serverVersion).isEqualTo("1.16")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(bugTwo.id)
                assertThat(it.poster).isEqualTo("posterTwo")
                assertThat(it.pluginId).isEqualTo(bugTwo.pluginId)
                assertThat(it.serverVersion).isEqualTo("1.17")
            }
            .verifyComplete()
    }

    @Test
    fun getBugsByPoster_shouldThrowClientIdNotFound() {
        `when`(headers.getFirst(anyString())).thenReturn(null)

        val returned = bugService.getBugsByPoster(req)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        StepVerifier.create(returned)
            .expectError(ClientIdNotFound::class.java)
            .verify()
    }

    @Test
    fun getBugsByPoster_shouldReturnBugs() {
        `when`(bugRepository.findByPoster(anyString())).thenReturn(Flux.just(bug, bugTwo))

        val returned = bugService.getBugsByPoster(req)

        verify(req, times(1)).headers
        verifyNoMoreInteractions(req)

        verify(headers, times(1)).getFirst(anyString())
        verifyNoMoreInteractions(headers)

        verify(bugRepository, times(1)).findByPoster(anyString())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(bug.id)
                assertThat(it.poster).isEqualTo("poster")
                assertThat(it.pluginId).isEqualTo(bug.pluginId)
                assertThat(it.serverVersion).isEqualTo("1.16")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(bugTwo.id)
                assertThat(it.poster).isEqualTo("posterTwo")
                assertThat(it.pluginId).isEqualTo(bugTwo.pluginId)
                assertThat(it.serverVersion).isEqualTo("1.17")
            }
            .verifyComplete()
    }

    @Test
    fun getBugsByPosterId_shouldReturnBugs() {
        `when`(bugRepository.findByPoster(anyString())).thenReturn(Flux.just(bug, bugTwo))

        val returned = bugService.getBugsByPosterId("test")

        verify(bugRepository, times(1)).findByPoster(anyString())
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(bug.id)
                assertThat(it.poster).isEqualTo("poster")
                assertThat(it.pluginId).isEqualTo(bug.pluginId)
                assertThat(it.serverVersion).isEqualTo("1.16")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(bugTwo.id)
                assertThat(it.poster).isEqualTo("posterTwo")
                assertThat(it.pluginId).isEqualTo(bugTwo.pluginId)
                assertThat(it.serverVersion).isEqualTo("1.17")
            }
            .verifyComplete()
    }

    @Test
    fun getBugs_shouldReturnBugs() {
        `when`(bugRepository.findAll()).thenReturn(Flux.just(bug, bugTwo))

        val returned = bugService.getBugs()

        verify(bugRepository, times(1)).findAll()
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(bug.id)
                assertThat(it.poster).isEqualTo("poster")
                assertThat(it.pluginId).isEqualTo(bug.pluginId)
                assertThat(it.serverVersion).isEqualTo("1.16")
            }
            .assertNext {
                assertThat(it.id).isEqualTo(bugTwo.id)
                assertThat(it.poster).isEqualTo("posterTwo")
                assertThat(it.pluginId).isEqualTo(bugTwo.pluginId)
                assertThat(it.serverVersion).isEqualTo("1.17")
            }
            .verifyComplete()
    }

    @Test
    fun removeBug_shouldThrowNotFound() {
        `when`(bugRepository.findById(any(UUID::class.java))).thenReturn(Mono.empty())

        val returned = bugService.removeBug(UUID.randomUUID())

        verify(bugRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
            .expectError(BugNotFound::class.java)
            .verify()
    }

    @Test
    fun removeBug_shouldReturnDeletedBug() {
        `when`(bugRepository.findById(any(UUID::class.java))).thenReturn(Mono.just(bug))
        `when`(bugRepository.delete(anyOrNull())).thenReturn(Mono.empty())

        val returned = bugService.removeBug(UUID.randomUUID())

        verify(bugRepository, times(1)).findById(any(UUID::class.java))
        verifyNoMoreInteractions(bugRepository)

        StepVerifier.create(returned)
            .assertNext {
                assertThat(it.id).isEqualTo(bug.id)
                assertThat(it.poster).isEqualTo("poster")
                assertThat(it.pluginId).isEqualTo(bug.pluginId)
                assertThat(it.serverVersion).isEqualTo("1.16")
            }
            .verifyComplete()
    }
}