package com.jtm.minecraft.core.domain.model

import java.util.*

data class PremiumDto(val accountId: UUID, val plugins: Array<UUID> = arrayOf())