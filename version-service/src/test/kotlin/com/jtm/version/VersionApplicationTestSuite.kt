package com.jtm.version

import com.jtm.version.data.service.FileSystemServiceTest
import com.jtm.version.data.service.VersionServiceTest
import com.jtm.version.entrypoint.controller.FileSystemControllerTest
import com.jtm.version.entrypoint.controller.VersionControllerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(value = [
    VersionControllerTest::class,
    VersionServiceTest::class,

    FileSystemServiceTest::class,
    FileSystemControllerTest::class
])
class VersionApplicationTestSuite