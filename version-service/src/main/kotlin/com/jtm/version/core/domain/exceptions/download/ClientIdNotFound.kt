package com.jtm.version.core.domain.exceptions.download

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Client Id not provided.")
class ClientIdNotFound: RuntimeException()