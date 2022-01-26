package com.jtm.plugin

import com.jtm.plugin.data.service.PluginServiceTest
import com.jtm.plugin.data.service.UpdateServiceTest
import com.jtm.plugin.entrypoint.controller.PluginControllerTest
import com.jtm.plugin.entrypoint.controller.UpdateControllerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(value = [
    PluginServiceTest::class,
    UpdateServiceTest::class,

    UpdateControllerTest::class,
    PluginControllerTest::class
])
class PluginServiceApplicationTestSuite