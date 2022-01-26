package com.jtm.plugin.core.domain.dto

import java.util.*

data class PluginDto(val id: UUID, val name: String? = "", val basic_description: String? = "", val description: String? = "",
                     val version: String? = "", val active: Boolean? = false, val price: Double? = 0.0)
