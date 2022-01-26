package com.jtm.plugin.core.domain.exception.plugin

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Value trying to update is null.")
class FailedUpdatePlugin: RuntimeException()