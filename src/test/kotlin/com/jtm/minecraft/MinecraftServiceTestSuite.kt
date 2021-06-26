package com.jtm.minecraft

import com.jtm.minecraft.core.usecase.FileHandlerTest
import com.jtm.minecraft.data.service.PluginServiceTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(value = [
    FileHandlerTest::class,
    PluginServiceTest::class,
])
class MinecraftServiceTestSuite