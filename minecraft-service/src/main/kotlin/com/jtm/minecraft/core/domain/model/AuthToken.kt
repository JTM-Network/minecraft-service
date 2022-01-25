package com.jtm.minecraft.core.domain.model

data class AuthToken(
    val token: String,
    val sentryDsn: String
)