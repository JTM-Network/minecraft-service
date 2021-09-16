package com.jtm.minecraft.core.domain.exceptions.plugin.bug

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.FOUND, reason = "Already reported a bug.")
class BugFound: RuntimeException()