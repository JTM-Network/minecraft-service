package com.jtm.minecraft.core.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("blacklisted_tokens")
data class BlacklistToken(
    @Id val token: String,
    val timestamp: Long = System.currentTimeMillis()
)