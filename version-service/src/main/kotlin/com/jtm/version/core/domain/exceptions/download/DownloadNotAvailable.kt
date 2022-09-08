package com.jtm.version.core.domain.exceptions.download

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid download request.")
class DownloadNotAvailable: RuntimeException()