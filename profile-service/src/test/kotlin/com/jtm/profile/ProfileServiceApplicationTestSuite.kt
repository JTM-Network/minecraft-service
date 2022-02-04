package com.jtm.profile

import com.jtm.profile.data.service.AuthServiceTest
import com.jtm.profile.data.service.ProfileServiceTest
import com.jtm.profile.entrypoint.controller.AuthControllerTest
import com.jtm.profile.entrypoint.controller.ProfileControllerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(value = [
    ProfileServiceTest::class,
    ProfileControllerTest::class,

    AuthServiceTest::class,
    AuthControllerTest::class
])
class ProfileServiceApplicationTestSuite