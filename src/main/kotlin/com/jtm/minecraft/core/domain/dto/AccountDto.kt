package com.jtm.minecraft.core.domain.dto

import java.util.*

data class AccountDto(
    var id: UUID,
    var username: String,
    var email: String,
    var password: String
)
