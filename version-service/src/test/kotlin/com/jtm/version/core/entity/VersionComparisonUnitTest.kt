package com.jtm.version.core.entity

import com.jtm.version.core.domain.entity.Version
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class VersionComparisonUnitTest {

    private val versionOne: Version = Version(pluginId = UUID.randomUUID(), pluginName = "Test", version = "1.0", changelog = "Version One change")
    private val versionTwo: Version = Version(pluginId = UUID.randomUUID(), pluginName = "Test #2", version = "1.1", changelog = "Version Two change")

    @Test
    fun compareTo_shouldReturnZero_whenVersionTwoIsGreater() {
        val compare = versionTwo.compareTo(versionOne)

        assertEquals(1, compare)
    }
}