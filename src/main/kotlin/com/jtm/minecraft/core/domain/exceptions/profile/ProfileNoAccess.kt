package com.jtm.minecraft.core.domain.exceptions.profile

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.FOUND, reason = "Profile does not have access to the plugin.")
class ProfileNoAccess: RuntimeException()