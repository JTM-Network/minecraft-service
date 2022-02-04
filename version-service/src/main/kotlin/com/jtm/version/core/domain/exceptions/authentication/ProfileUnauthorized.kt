package com.jtm.version.core.domain.exceptions.authentication

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Profile unauthorized.")
class ProfileUnauthorized: RuntimeException()