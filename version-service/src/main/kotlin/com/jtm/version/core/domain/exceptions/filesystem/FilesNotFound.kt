package com.jtm.version.core.domain.exceptions.filesystem

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Files not found.")
class FilesNotFound: RuntimeException()