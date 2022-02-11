package com.jtm.plugin

import com.jtm.plugin.data.service.*
import com.jtm.plugin.entrypoint.controller.*
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
])
class PluginServiceApplicationTestSuite