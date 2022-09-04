package com.jtm.version

import com.jtm.version.core.usecase.ProfileAuthorizationTest
import com.jtm.version.data.service.*
import com.jtm.version.entrypoint.controller.DownloadRequestControllerTest
import com.jtm.version.entrypoint.controller.FileSystemControllerTest
import com.jtm.version.entrypoint.controller.UpdateControllerUnitTest
import com.jtm.version.entrypoint.controller.VersionControllerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(value = [
    ProfileAuthorizationTest::class,

    VersionControllerTest::class,
    VersionServiceTest::class,

    FileSystemServiceTest::class,
    FileSystemControllerTest::class,

    DownloadRequestServiceTest::class,
    DownloadRequestControllerTest::class,

    DownloadServiceTest::class,

    UpdateServiceUnitTest::class,
    UpdateControllerUnitTest::class
])
class VersionApplicationTestSuite