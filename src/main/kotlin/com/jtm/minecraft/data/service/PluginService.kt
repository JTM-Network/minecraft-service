package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.usecase.repository.PluginRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PluginService @Autowired constructor(private val pluginRepository: PluginRepository) {

}