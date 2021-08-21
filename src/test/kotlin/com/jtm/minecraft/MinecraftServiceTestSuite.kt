package com.jtm.minecraft

import com.jtm.minecraft.core.usecase.file.FileHandlerTest
import com.jtm.minecraft.core.usecase.token.AccountTokenProviderTest
import com.jtm.minecraft.data.manager.AuthenticationManager
import com.jtm.minecraft.data.manager.AuthenticationManagerTest
import com.jtm.minecraft.data.security.SecurityContextRepositoryTest
import com.jtm.minecraft.data.service.AuthServiceTest
import com.jtm.minecraft.data.service.BlacklistTokenServiceTest
import com.jtm.minecraft.data.service.PluginServiceTest
import com.jtm.minecraft.data.service.ProfileServiceTest
import com.jtm.minecraft.data.service.plugin.AccessServiceTest
import com.jtm.minecraft.entrypoint.controller.AuthControllerTest
import com.jtm.minecraft.entrypoint.controller.PluginControllerTest
import com.jtm.minecraft.entrypoint.controller.ProfileControllerTest
import com.jtm.minecraft.entrypoint.controller.plugin.AccessControllerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(value = [
    FileHandlerTest::class,
    AccountTokenProviderTest::class,

    PluginServiceTest::class,
    ProfileServiceTest::class,
    BlacklistTokenServiceTest::class,
    AuthServiceTest::class,
    AccessServiceTest::class,

    SecurityContextRepositoryTest::class,
    AuthenticationManagerTest::class,

    PluginControllerTest::class,
    ProfileControllerTest::class,
    AuthControllerTest::class,
    AccessControllerTest::class
])
class MinecraftServiceTestSuite