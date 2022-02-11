package com.jtm.plugin.core.domain.exception.profile

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "You have to wait to post again.")
class NotAllowedToPost: RuntimeException()