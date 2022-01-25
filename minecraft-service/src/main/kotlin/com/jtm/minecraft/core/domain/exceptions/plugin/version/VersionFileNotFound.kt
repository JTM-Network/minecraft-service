package com.jtm.minecraft.core.domain.exceptions.plugin.version

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Version file not found.")
class VersionFileNotFound: RuntimeException()