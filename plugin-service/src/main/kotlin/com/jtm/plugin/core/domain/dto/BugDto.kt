package com.jtm.plugin.core.domain.dto

import java.util.*

data class BugDto(val pluginId: UUID,
                  val serverVersion: String,
                  val pluginVersion: String,
                  val recreateComment: String,
                  val happensComment: String)
