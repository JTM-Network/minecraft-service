package com.jtm.plugin.core.domain.exception.bug

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Bug not found.")
class BugNotFound: RuntimeException()