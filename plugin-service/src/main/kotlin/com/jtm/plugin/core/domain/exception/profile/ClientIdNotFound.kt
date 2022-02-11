package com.jtm.plugin.core.domain.exception.profile

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Client ID not found.")
class ClientIdNotFound: RuntimeException()