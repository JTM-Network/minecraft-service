package com.jtm.minecraft.core.domain.exceptions.plugin

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Plugin Id not found in request.")
class PluginHeaderMissing: RuntimeException()