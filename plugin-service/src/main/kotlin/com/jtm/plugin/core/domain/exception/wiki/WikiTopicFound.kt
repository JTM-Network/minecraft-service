package com.jtm.plugin.core.domain.exception.wiki

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FOUND, reason = "Wiki topic already found.")
class WikiTopicFound: RuntimeException()