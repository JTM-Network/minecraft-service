package com.jtm.minecraft.core.domain.model

import java.util.*

data class PluginIntent(val total: Double = 0.0, val currency: String = "gbp", val plugins: List<UUID> = listOf())
