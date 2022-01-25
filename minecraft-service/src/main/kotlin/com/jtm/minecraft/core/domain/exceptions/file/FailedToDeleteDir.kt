package com.jtm.minecraft.core.domain.exceptions.file

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Failed to delete directory.")
class FailedToDeleteDir: RuntimeException()