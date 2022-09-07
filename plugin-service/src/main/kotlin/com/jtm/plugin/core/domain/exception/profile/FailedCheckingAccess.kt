package com.jtm.plugin.core.domain.exception.profile

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, reason = "Failed checking the access for the user.")
class FailedCheckingAccess: RuntimeException()