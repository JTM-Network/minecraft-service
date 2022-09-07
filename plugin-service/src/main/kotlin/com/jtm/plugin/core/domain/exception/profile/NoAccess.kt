package com.jtm.plugin.core.domain.exception.profile

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "No access to plugin.")
class NoAccess: RuntimeException()