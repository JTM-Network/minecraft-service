package com.jtm.minecraft.core.domain.exceptions.plugin

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Plugin is a premium resource")
class PluginIsPremium: RuntimeException()