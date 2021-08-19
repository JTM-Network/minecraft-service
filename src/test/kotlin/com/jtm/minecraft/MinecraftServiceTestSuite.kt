package com.jtm.minecraft

import com.jtm.minecraft.core.usecase.file.FileHandlerTest
import com.jtm.minecraft.core.usecase.token.AccountTokenProviderTest
import com.jtm.minecraft.data.service.PluginServiceTest
import com.jtm.minecraft.data.service.ProfileServiceTest
import com.jtm.minecraft.entrypoint.controller.PluginControllerTest
import com.jtm.minecraft.entrypoint.controller.ProfileControllerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(value = [
    FileHandlerTest::class,
    AccountTokenProviderTest::class,

    PluginServiceTest::class,
    ProfileServiceTest::class,

    PluginControllerTest::class,
    ProfileControllerTest::class
])
class MinecraftServiceTestSuite