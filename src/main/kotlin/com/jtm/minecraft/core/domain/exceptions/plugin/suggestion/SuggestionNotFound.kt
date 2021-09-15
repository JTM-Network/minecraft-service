package com.jtm.minecraft.core.domain.exceptions.plugin.suggestion

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Suggestion not found.")
class SuggestionNotFound: RuntimeException()