package com.jtm.plugin.core.domain.exception.plugin

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Plugin not found.")
class PluginNotFound: RuntimeException()