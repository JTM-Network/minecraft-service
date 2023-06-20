package com.jtm.plugin

import com.jtm.plugin.data.service.*
import com.jtm.plugin.data.service.image.ImageServiceUnitTest
import com.jtm.plugin.entrypoint.controller.*
import com.jtm.plugin.entrypoint.controller.image.ImageControllerUnitTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(value = [
    PluginServiceTest::class,
    UpdateServiceTest::class,

    UpdateControllerTest::class,
    PluginControllerTest::class,

    WikiServiceTest::class,
    WikiControllerTest::class,

    SuggestionServiceTest::class,
    BugServiceTest::class,
    ReviewServiceTest::class,

    SuggestionControllerTest::class,
    BugControllerTest::class,
    ReviewControllerTest::class,

    ImageServiceUnitTest::class,

    ImageControllerUnitTest::class,
])
class PluginServiceApplicationTestSuite