package com.jtm.plugin.core.domain.exception.plugin

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Basic plugin information is null")
class PluginInformationNull: RuntimeException()