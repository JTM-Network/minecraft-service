package com.jtm.profile.core.domain.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FOUND, reason = "Profile has already been banned.")
class ProfileAlreadyBanned: RuntimeException()