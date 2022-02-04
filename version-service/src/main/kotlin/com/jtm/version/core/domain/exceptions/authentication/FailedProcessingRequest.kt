package com.jtm.version.core.domain.exceptions.authentication

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Failed processing request.")
class FailedProcessingRequest: RuntimeException()