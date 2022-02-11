package com.jtm.plugin.core.domain.exception.review

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FOUND, reason = "You can only post 1 review per plugin.")
class OnlyOneReview: RuntimeException()