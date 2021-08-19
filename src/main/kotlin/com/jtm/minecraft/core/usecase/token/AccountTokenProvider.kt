package com.jtm.minecraft.core.usecase.token

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountTokenProvider {

    @Value("\${security.jwt.access-key:accessKey}")
    lateinit var accessKey: String

    @Value("\${security.jwt.plugin-key:pluginKey}")
    lateinit var pluginKey: String

    fun resolveToken(bearer: String): String {
        return bearer.replace("Bearer ", "")
    }

    fun getAccountId(token: String): UUID? {
        val claims = Jwts.parser().setSigningKey(accessKey).parseClaimsJws(token)
        return UUID.fromString(claims.body["id"].toString())
    }

    fun getAccountEmail(token: String): String? {
        return Jwts.parser().setSigningKey(accessKey).parseClaimsJws(token).body.subject
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
        val claims = Jwts.parser().setSigningKey(pluginKey).parseClaimsJws(token)
        return UUID.fromString(claims.body["id"].toString())

    }

    fun getPluginAccountEmail(token: String): String? {
        return Jwts.parser().setSigningKey(pluginKey).parseClaimsJws(token).body.subject
    }

    fun getPluginId(token: String): UUID? {
        val claims = Jwts.parser().setSigningKey(pluginKey).parseClaimsJws(token)
        return UUID.fromString(claims.body["plugin_id"].toString())
    }
}