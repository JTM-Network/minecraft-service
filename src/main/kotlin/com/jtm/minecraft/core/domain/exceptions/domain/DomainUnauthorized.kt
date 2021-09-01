package com.jtm.minecraft.core.domain.exceptions.domain

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Domain is unauthorized.")
class DomainUnauthorized: RuntimeException()