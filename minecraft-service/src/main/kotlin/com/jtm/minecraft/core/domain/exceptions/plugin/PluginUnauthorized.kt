package com.jtm.minecraft.core.domain.exceptions.plugin

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Not authorized to use that plugin.")
class PluginUnauthorized: RuntimeException()