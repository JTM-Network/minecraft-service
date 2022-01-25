package com.jtm.minecraft.core.domain.exceptions.domain

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.FOUND, reason = "Address has been found.")
class AddressFound: RuntimeException()