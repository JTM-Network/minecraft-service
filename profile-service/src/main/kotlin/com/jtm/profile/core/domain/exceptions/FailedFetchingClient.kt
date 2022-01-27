package com.jtm.profile.core.domain.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Failed to fetch client Id")
class FailedFetchingClient: RuntimeException()