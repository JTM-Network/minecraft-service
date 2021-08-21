package com.jtm.minecraft.core.domain.exceptions.plugin.review

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.FOUND, reason = "Review found.")
class ReviewFound: RuntimeException()