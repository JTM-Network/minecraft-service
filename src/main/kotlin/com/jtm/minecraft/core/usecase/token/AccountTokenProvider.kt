package com.jtm.minecraft.core.usecase.token

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountTokenProvider {

    @Value("\${security.jwt.access-key:accessKey}")
    lateinit var accessKey: String

    fun resolveToken(bearer: String): String {
        return bearer.replace("Bearer ", "")
    }

    fun getAccountId(token: String): UUID {
        val claims = Jwts.parser().setSigningKey(accessKey).parseClaimsJws(token)
        return UUID.fromString(claims.body["id"].toString())
    }

    fun getAccountEmail(token: String): String {
        return Jwts.parser().setSigningKey(accessKey).parseClaimsJws(token).body.subject
    }
}