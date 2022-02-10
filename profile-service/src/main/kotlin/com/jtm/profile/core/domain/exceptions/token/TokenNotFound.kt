package com.jtm.profile.core.domain.exceptions.token

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Token not found.")
class TokenNotFound: RuntimeException()