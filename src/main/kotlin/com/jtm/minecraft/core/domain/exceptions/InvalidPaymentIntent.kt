package com.jtm.minecraft.core.domain.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Invalid payment intent.")
class InvalidPaymentIntent: RuntimeException()