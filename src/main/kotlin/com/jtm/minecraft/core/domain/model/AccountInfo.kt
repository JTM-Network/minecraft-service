package com.jtm.minecraft.core.domain.model

import java.util.*

data class AccountInfo(
    val id: UUID,
    val username: String,
    val email: String,
    val password: String,
    val verified: Boolean,
    )