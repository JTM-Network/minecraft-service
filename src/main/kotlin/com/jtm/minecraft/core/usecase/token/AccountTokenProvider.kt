package com.jtm.minecraft.core.usecase.token

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountTokenProvider {

    @Value("\${security.jwt.api-key:apiKey}")
    lateinit var apiKey: String

    @Value("\${security.jwt.plugin-key:pluginKey}")
    lateinit var pluginKey: String

    fun resolveToken(bearer: String): String {
        return bearer.replace("Bearer ", "")
    }

    fun getAccountId(token: String): UUID? {
        return try {
            val claims = Jwts.parser().setSigningKey(apiKey).parseClaimsJws(token)
            UUID.fromString(claims.body["id"].toString())
        } catch (ex: SignatureException) {
            null
        }
    }

    fun getAccountEmail(token: String): String? {
        return try {
            Jwts.parser().setSigningKey(apiKey).parseClaimsJws(token).body.subject
        } catch (ex: SignatureException) {
            null
        }
    }

    fun createPluginToken(id: UUID, email: String, pluginId: UUID): String {
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS256, pluginKey)
            .setSubject(email)
            .claim("id", id.toString())
            .claim("plugin_id", pluginId.toString())
            .compact()
    }

    fun getPluginAccountId(token: String): UUID? {
        return try {
            val claims = Jwts.parser().setSigningKey(pluginKey).parseClaimsJws(token)
            UUID.fromString(claims.body["id"].toString())
        } catch (ex: SignatureException) {
            null
        }
    }

    fun getPluginAccountEmail(token: String): String? {
        return try {
            Jwts.parser().setSigningKey(pluginKey).parseClaimsJws(token).body.subject
        } catch (ex: SignatureException) {
            null
        }
    }

    fun getPluginId(token: String): UUID? {
        return try {
            val claims = Jwts.parser().setSigningKey(pluginKey).parseClaimsJws(token)
            UUID.fromString(claims.body["plugin_id"].toString())
        } catch (ex: SignatureException) {
            null
        }
    }
}