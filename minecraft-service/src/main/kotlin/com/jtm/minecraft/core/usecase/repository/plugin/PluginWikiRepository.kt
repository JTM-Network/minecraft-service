package com.jtm.minecraft.core.usecase.repository.plugin

import com.jtm.minecraft.core.domain.entity.plugin.PluginWiki
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PluginWikiRepository: ReactiveMongoRepository<PluginWiki, UUID>