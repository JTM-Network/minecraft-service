package com.jtm.minecraft.core.usecase.token

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountTokenProvider {

    @Value("\${security.jwt.access-key:accessKey}")
    lateinit var accessKey: String

    @Value("\${security.jwt.api-key:apiKey}")
    lateinit var apiKey: String

    @Value("\${security.jwt.plugin-key:pluginKey}")
    lateinit var pluginKey: String

    /**
     * Resolve the token from bearer
     *
     * @return the JWT token, if throws {@link SignatureException} return null
     */
    fun resolveToken(bearer: String): String {
        return bearer.replace("Bearer ", "")
    }

    /**
     * Return account id from parsing access token
     *
     * @return the account id, if throws {@link SignatureException} return null
     */
    fun getAccountId(token: String): UUID? {
        return try {
            val claims = Jwts.parser().setSigningKey(accessKey).parseClaimsJws(token)
            UUID.fromString(claims.body["id"].toString())
        } catch (ex: SignatureException) {
            null
        }
    }

    /**
     * Return email from parsing access token
     *
     * @return the account email, if throws {@link SignatureException} return null
     */
    fun getAccountEmail(token: String): String? {
        return try {
            Jwts.parser().setSigningKey(accessKey).parseClaimsJws(token).body.subject
        } catch (ex: SignatureException) {
            null
        }
    }

    /**
     * Return account id from parsing api token
     *
     * @return the account id, if throws {@link SignatureException} return null
     */
    fun getApiAccountId(token: String): UUID? {
        return try {
            val claims = Jwts.parser().setSigningKey(apiKey).parseClaimsJws(token)
            UUID.fromString(claims.body["id"].toString())
        } catch (ex: SignatureException) {
            null
        }
    }

    /**
     * Return account email from parsing api token
     *
     * @return the account email, if throws {@link SignatureException} return null
     */
    fun getApiAccountEmail(token: String): String? {
        return try {
            Jwts.parser().setSigningKey(apiKey).parseClaimsJws(token).body.subject
        } catch (ex: SignatureException) {
            null
        }
    }

    /**
     * Create plugin JWT token
     *
     * @param id - the account id
     * @param email - the account email
     * @param pluginId -  the plugin id
     * @return the JWT token, if throws {@link SignatureException} return null
     */
    fun createPluginToken(id: UUID, email: String, pluginId: UUID): String {
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS256, pluginKey)
            .setSubject(email)
            .claim("id", id.toString())
            .claim("plugin_id", pluginId.toString())
            .compact()
    }

    /**
     * Return account id from parsing plugin token
     *
     * @return the account id, if throws {@link SignatureException} return null
     */
    fun getPluginAccountId(token: String): UUID? {
        return try {
            val claims = Jwts.parser().setSigningKey(pluginKey).parseClaimsJws(token)
            UUID.fromString(claims.body["id"].toString())
        } catch (ex: SignatureException) {
            null
        }
    }

    /**
     * Return the account email from parsing the plugin token
     *
     * @return the account email
     */
    fun getPluginAccountEmail(token: String): String? {
        return try {
            Jwts.parser().setSigningKey(pluginKey).parseClaimsJws(token).body.subject
        } catch (ex: SignatureException) {
            null
        }
    }

    /**
     * Return the plugin id from parsing the plugin token
     *
     * @return the plugin id, if throws {@link SignatureException} return null
     */
    fun getPluginId(token: String): UUID? {
        return try {
            val claims = Jwts.parser().setSigningKey(pluginKey).parseClaimsJws(token)
            UUID.fromString(claims.body["plugin_id"].toString())
        } catch (ex: SignatureException) {
            null
        }
    }
}