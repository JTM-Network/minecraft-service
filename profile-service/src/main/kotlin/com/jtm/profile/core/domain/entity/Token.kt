package com.jtm.profile.core.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("tokens")
data class Token(@Id val id: UUID = UUID.randomUUID(),
                 val token: String,
                 var clientId: String,
                 var uses: Int = 0,
                 var lastUsed: Long = 0,
                 var blacklisted: Boolean = false,
                 val generated: Long = System.currentTimeMillis()) {

    fun addUse(): Token {
        this.uses += 1
        this.lastUsed = System.currentTimeMillis()
        return this
    }

    fun removeUse(): Token {
        this.uses -= 1
        this.lastUsed = System.currentTimeMillis()
        return this
    }

    fun blacklist(): Token {
        this.blacklisted = true
        return this
    }
}
