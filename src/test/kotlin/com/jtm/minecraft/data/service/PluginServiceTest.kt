package com.jtm.minecraft.data.service

import com.jtm.minecraft.core.domain.dto.PluginDto
import com.jtm.minecraft.core.usecase.repository.PluginRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Mono
import java.util.*

@RunWith(SpringRunner::class)
class PluginServiceTest {

    private val pluginRepository = mock(PluginRepository::class.java)
    private val pluginService = PluginService(pluginRepository)

    @Test fun insertPluginTest() {
        val returned = pluginService.insertPlugin(PluginDto("test", "test"))
    }

    @Test fun updatePluginTest() {

        val returned = pluginService.updatePlugin(PluginDto("test", "test"))
    }

    @Test fun getPluginTest() {
        val returned = pluginService.getPlugin(UUID.randomUUID())
    }

    @Test fun getPluginByNameTest() {
        val returned = pluginService.getPluginByName("test")
    }

    @Test fun getPluginsTest() {

        val returned = pluginService.getPlugins()
    }

    @Test fun removePluginTest() {
        val returned = pluginService.removePlugin(UUID.randomUUID())
    }
}