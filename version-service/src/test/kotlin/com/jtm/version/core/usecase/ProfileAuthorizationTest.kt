package com.jtm.version.core.usecase

import com.jtm.version.core.domain.exceptions.authentication.FailedProcessingRequest
import com.jtm.version.core.domain.exceptions.authentication.ProfileUnauthorized
import com.jtm.version.core.usecase.auth.ProfileAuthorization
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner
import reactor.test.StepVerifier
import java.util.*

@RunWith(SpringRunner::class)
class ProfileAuthorizationTest {

    private val mockWebServer = MockWebServer()
    private lateinit var profileAuthorization: ProfileAuthorization

    @Before
    fun setup() {
        mockWebServer.start()

        val baseUrl = String.format("http://localhost:%s", mockWebServer.port)
        profileAuthorization = ProfileAuthorization(baseUrl)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        println("Shutdown ran.")
    }

    @Test
    fun authorize_thenUnauthorized() {
        mockWebServer.enqueue(MockResponse().setResponseCode(401))

        val returned = profileAuthorization.authorize("clientId", UUID.randomUUID())

        StepVerifier.create(returned)
            .expectError(ProfileUnauthorized::class.java)
            .verify()
    }

    @Test
    fun authorize_thenFailedProcessing() {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val returned = profileAuthorization.authorize("clientId", UUID.randomUUID())

        StepVerifier.create(returned)
            .expectError(FailedProcessingRequest::class.java)
            .verify()
    }

    @Test
    fun authorize() {
        mockWebServer.enqueue(MockResponse().setResponseCode(200))

        val returned = profileAuthorization.authorize("clientId", UUID.randomUUID())

        StepVerifier.create(returned)
            .verifyComplete()
    }
}