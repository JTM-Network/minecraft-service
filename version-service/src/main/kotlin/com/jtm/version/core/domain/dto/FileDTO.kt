package com.jtm.version.core.domain.dto

import java.time.OffsetDateTime

data class FileDTO(val name: String, val path: String, val size: Long, val lastModified: OffsetDateTime, val extension: String, val isFile: Boolean, val isDirectory: Boolean)