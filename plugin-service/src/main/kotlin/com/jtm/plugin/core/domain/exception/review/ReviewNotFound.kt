package com.jtm.plugin.core.domain.exception.review

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Review not found.")
class ReviewNotFound: RuntimeException()