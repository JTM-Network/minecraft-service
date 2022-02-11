package com.jtm.plugin.core.domain.exception.suggestion

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Suggestion not found.")
class SuggestionNotFound: RuntimeException()