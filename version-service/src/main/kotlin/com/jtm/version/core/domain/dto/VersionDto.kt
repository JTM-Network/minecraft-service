package com.jtm.version.core.domain.dto

import org.springframework.http.codec.multipart.FilePart
import java.util.*

data class VersionDto(val pluginId: UUID,
                      val name: String,
                      val file: FilePart? = null,
                      val version: String,
                      val changelog: String = "")
