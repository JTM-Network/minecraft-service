package com.jtm.plugin.core.domain.dto

import java.util.*

data class ReviewDto(val pluginId: UUID, val rating: Double, val comment: String)
