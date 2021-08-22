package com.jtm.minecraft.core.domain.dto

import org.springframework.http.codec.multipart.FilePart
import java.util.*

data class PluginVersionDto(
    val pluginId: UUID,
    val file: FilePart,
    val version: String,
    val changelog: String,
)
