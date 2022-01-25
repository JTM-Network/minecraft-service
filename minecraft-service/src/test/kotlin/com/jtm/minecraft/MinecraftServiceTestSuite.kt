package com.jtm.minecraft

import com.jtm.minecraft.core.usecase.file.FileHandlerTest
import com.jtm.minecraft.core.usecase.token.AccountTokenProviderTest
import com.jtm.minecraft.data.manager.AuthenticationManagerTest
import com.jtm.minecraft.data.security.SecurityContextRepositoryTest
import com.jtm.minecraft.data.service.*
import com.jtm.minecraft.data.service.domain.AddressServiceTest
import com.jtm.minecraft.data.service.domain.DomainIpServiceTest
import com.jtm.minecraft.data.service.domain.DomainServiceTest
import com.jtm.minecraft.data.service.plugin.*
import com.jtm.minecraft.entrypoint.controller.AuthControllerTest
import com.jtm.minecraft.entrypoint.controller.PluginControllerTest
import com.jtm.minecraft.entrypoint.controller.ProfileControllerTest
import com.jtm.minecraft.entrypoint.controller.domain.AddressControllerTest
import com.jtm.minecraft.entrypoint.controller.domain.DomainControllerTest
import com.jtm.minecraft.entrypoint.controller.plugin.*
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
    DomainIpServiceTest::class,

    AccessServiceTest::class,
    VersionServiceTest::class,
    DownloadServiceTest::class,
    ReviewServiceTest::class,
    ImageServiceTest::class,
    IntentServiceTest::class,
    WikiServiceTest::class,
    SuggestionServiceTest::class,
    BugServiceTest::class,

    DomainServiceTest::class,
    AddressServiceTest::class,

    SecurityContextRepositoryTest::class,
    AuthenticationManagerTest::class,

    PluginControllerTest::class,
    ProfileControllerTest::class,
    AuthControllerTest::class,

    AccessControllerTest::class,
    VersionControllerTest::class,
    ImageControllerTest::class,
    ReviewControllerTest::class,
    IntentControllerTest::class,
    WikiControllerTest::class,
    SuggestionControllerTest::class,
    BugControllerTest::class,

    AddressControllerTest::class,
    DomainControllerTest::class
])
class MinecraftServiceTestSuite