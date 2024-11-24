package com.whakaara.feature.stopwatch

import com.whakaara.feature.stopwatch.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest {
    @Test
    fun `converting milliseconds to correct format for stopwatch`() {
        // Given
        val millis = 12312312.toLong()
        val millisFormatted = "03:25:12:312"

        // When
        val formattedTime = DateUtils.formatTimeForStopwatch(millis = millis)

        // Then
        assertEquals(formattedTime, millisFormatted)
    }

    @Test
    fun `converting milliseconds to correct format for stopwatch lap with hours`() {
        // Given
        val millis = 12312312L
        val millisFormatted = "03:25:12:312"

        // When
        val formattedTimer = DateUtils.formatTimeForStopwatchLap(millis = millis)

        // Then
        assertEquals(millisFormatted, formattedTimer)
    }

    @Test
    fun `converting milliseconds to correct format for stopwatch lap without hours`() {
        // Given
        val millis = 1231231L
        val millisFormatted = "20:31:231"

        // When
        val formattedTimer = DateUtils.formatTimeForStopwatchLap(millis = millis)

        // Then
        assertEquals(millisFormatted, formattedTimer)
    }
}
