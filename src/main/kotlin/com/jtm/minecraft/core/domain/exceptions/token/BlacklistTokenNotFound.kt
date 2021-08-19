package com.jtm.minecraft.core.domain.exceptions.token

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Blacklist token not found")
class BlacklistTokenNotFound: RuntimeException()