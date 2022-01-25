package com.jtm.minecraft.core.domain.exceptions.domain

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Address not found.")
class AddressNotFound: RuntimeException()