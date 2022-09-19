package com.jtm.plugin.core.domain.exception.image

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, reason = "Failed to delete image.")
class FailedImageDeletion: RuntimeException()