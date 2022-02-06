package com.jtm.version.core.domain.exceptions.download

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Download link not found.")
class DownloadLinkNotFound: RuntimeException()