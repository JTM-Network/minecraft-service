package com.jtm.minecraft.core.domain.exceptions.file

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Folder not found.")
class FolderNotFound: RuntimeException()