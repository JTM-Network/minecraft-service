package com.jtm.minecraft.core.usecase.token

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import junit.framework.Assert.assertNotNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class AccountTokenProviderTest {

    private val tokenProvider = AccountTokenProvider()
    private val uuid = UUID.randomUUID()
    private lateinit var access_token: String
    private lateinit var api_token: String
    private lateinit var plugin_token: String

    @Before
    fun setup() {
        tokenProvider.accessKey = "accessKey"
        tokenProvider.apiKey = "apiKey"
        tokenProvider.pluginKey = "pluginKey"

        access_token = createToken(tokenProvider.accessKey, uuid, "test@gmail.com", UUID.randomUUID())
        api_token = createToken(tokenProvider.accessKey, uuid, "test@gmail.com", UUID.randomUUID())
        plugin_token = createToken(tokenProvider.pluginKey, uuid, "test@gmail.com", UUID.randomUUID())
    }

    @Test
    fun resolveTokenTest() {
        val token = tokenProvider.resolveToken("Bearer test")

        assertThat(token).isEqualTo("test")
    }

    @Test
    fun getAccountIdTest() {
        val id = tokenProvider.getAccountId(access_token)

        assertThat(id).isEqualTo(uuid)
    }

    @Test
    fun getAccountEmailTest() {
        val email = tokenProvider.getAccountEmail(access_token)

        assertThat(email).isEqualTo("test@gmail.com")
    }

    @Test
    fun createPluginTokenTest() {
        val token = tokenProvider.createPluginToken(UUID.randomUUID(), "plugin@gmail.com", UUID.randomUUID())

        assertNotNull(token)
    }

    @Test
    fun getPluginAccountIdTest() {
        val id = tokenProvider.getPluginAccountId(plugin_token)

        assertThat(id).isEqualTo(uuid)
    }

    @Test
    fun getPluginAccountEmailTest() {
        val email = tokenProvider.getPluginAccountEmail(plugin_token)

        assertThat(email).isEqualTo("test@gmail.com")
    }

    @Test
    fun getPluginIdTest() {
        val pluginId = tokenProvider.getPluginId(plugin_token)

        assertThat(pluginId).isInstanceOf(UUID::class.java)
    }

    private fun createToken(key: String, id: UUID, email: String, pluginId: UUID): String {
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS256, key)
            .setSubject(email)
            .claim("id", id.toString())
            .claim("plugin_id", pluginId.toString())
            .compact()
    }
}