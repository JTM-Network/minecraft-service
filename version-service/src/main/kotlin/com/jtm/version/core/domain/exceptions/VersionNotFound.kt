package com.jtm.version.core.domain.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Version not found.")
class VersionNotFound: RuntimeException()