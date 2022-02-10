package com.jtm.profile.core.usecase.provider

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenProvider {

    @Value("\${jwt.secret-key:secretKey}")
    lateinit var secretKey: String

    /**
     * This will resolve the token from the Authorization Header value,
     * prefixed with "Bearer"
     *
     * @param bearer        the Authorization Header value
     * @return              the JWT token
     */
    fun resolveToken(bearer: String): String {
        return bearer.replace("Bearer ", "")
    }

    /**
     * This will create a JWT token.
     *
     * @param accountId     this account identifier
     * @return              the JWT token
     */
    fun createToken(accountId: String): String {
        val issuedAt = Date(System.currentTimeMillis())
        return Jwts.builder()
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .setSubject(accountId)
            .setIssuedAt(issuedAt)
            .compact()
    }

    /**
     * This will return the account identifier from the token.
     *
     * @param token         the JWT token
     * @return              the account identifier
     */
    fun getAccountId(token: String): String? {
        return try {
            val claim = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            claim.body.subject
        } catch (ex: SignatureException) {
            null
        }
    }
}