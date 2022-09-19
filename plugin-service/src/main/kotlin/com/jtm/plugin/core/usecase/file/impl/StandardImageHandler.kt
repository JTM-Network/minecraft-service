package com.jtm.plugin.core.usecase.file.impl

import com.jtm.plugin.core.usecase.file.ImageHandlerImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class StandardImageHandler @Autowired constructor(@Value("\${images.path:/images}") path: String): ImageHandlerImpl(path)