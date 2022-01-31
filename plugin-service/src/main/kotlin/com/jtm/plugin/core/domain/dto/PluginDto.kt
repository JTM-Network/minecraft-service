package com.jtm.plugin.core.domain.dto

data class PluginDto(val name: String? = "", val basic_description: String? = "", val description: String? = "",
                     val version: String? = "", val active: Boolean? = false, val price: Double? = 0.0)
