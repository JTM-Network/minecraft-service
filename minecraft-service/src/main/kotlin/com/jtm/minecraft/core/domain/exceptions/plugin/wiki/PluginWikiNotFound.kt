package com.jtm.minecraft.core.domain.exceptions.plugin.wiki

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Plugin wiki not found.")
class PluginWikiNotFound: RuntimeException()