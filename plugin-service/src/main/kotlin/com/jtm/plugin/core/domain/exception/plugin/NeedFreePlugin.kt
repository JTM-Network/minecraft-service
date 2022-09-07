package com.jtm.plugin.core.domain.exception.plugin

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Plugin needs to be free to be downloaded.")
class NeedFreePlugin: RuntimeException()