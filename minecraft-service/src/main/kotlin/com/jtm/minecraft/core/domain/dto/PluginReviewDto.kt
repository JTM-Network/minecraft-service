package com.jtm.minecraft.core.domain.dto

import java.util.*

data class PluginReviewDto(
    val pluginId: UUID,
    val rating: Double,
    val comment: String
)