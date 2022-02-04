package com.jtm.version.core.domain.exceptions.version

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FOUND, reason = "Version already found.")
class VersionFound: RuntimeException()