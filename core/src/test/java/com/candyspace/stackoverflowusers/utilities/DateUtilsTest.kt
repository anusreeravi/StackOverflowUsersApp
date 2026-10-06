package com.candyspace.stackoverflowusers.utilities

import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest {

    @Test
    fun `formatEpochToReadableDate formats zero or negative epoch to NA`() {
        assertEquals("N/A", 0L.formatEpochToReadableDate())
        assertEquals("N/A", (-100L).formatEpochToReadableDate())
    }

    @Test
    fun `formatEpochToReadableDate formats valid epoch timestamp`() {
        val epochSeconds = 1538654400L // Oct 04, 2018
        val result = epochSeconds.formatEpochToReadableDate()
        assert(result.contains("2018"))
    }
}
