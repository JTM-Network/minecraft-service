package com.jtm.plugin.core.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Basic information not found.")
class BasicInfoNotFound: RuntimeException()