package com.jtm.profile.core.domain.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Profile is unauthorized to use this plugin.")
class ProfileUnauthorized: RuntimeException()