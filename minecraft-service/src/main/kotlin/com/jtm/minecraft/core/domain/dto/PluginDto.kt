package com.jtm.minecraft.core.domain.dto

data class PluginDto(
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var active: Boolean = false,
)
