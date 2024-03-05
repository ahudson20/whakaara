package com.app.whakaara.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {
    @Test
    fun `convert seconds to time until alarm string`() {
        // Given
        val time: Long = 111222333

        // When
        val timeString = DateUtils.convertSecondsToHMm(time)

        // Then
        assertEquals("Alarm in 7 hours 5 minutes", timeString)
    }

    @Test
    fun `convert time to 24 hour format`() {
        // Given
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 34)
            set(Calendar.SECOND, 0)
        }

        // When
        val time24HourFormat = DateUtils.getAlarmTimeFormatted(date = date, is24HourFormatEnabled = true)

        // Then
        assertEquals("14:34", time24HourFormat)
    }

    @Test
    fun `convert time to 12 hour format`() {
        // Given
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 34)
            set(Calendar.SECOND, 0)
        }

        // When
        val time24HourFormat = DateUtils.getAlarmTimeFormatted(date = date, is24HourFormatEnabled = false)

        // Then
        assertEquals("2:34 PM", time24HourFormat)
    }

    @Test
    fun `get initial time to alarm when alarm disabled`() {
        // Given + When
        val initial = DateUtils.getInitialTimeToAlarm(false, Calendar.getInstance())

        // Then
        assertEquals("Off", initial)
    }

    @Test
    fun `convert Int representing hours to milliseconds`() {
        // Given
        val hours = 1

        // When
        val millis = DateUtils.hoursToMilliseconds(hours = hours)

        // Then
        assertEquals(millis, 3600000) // 1 hour = 3600 seconds = 3600 * 1000 milliseconds
    }

    @Test
    fun `convert Int representing minutes to milliseconds`() {
        // Given
        val minutes = 1

        // When
        val millis = DateUtils.minutesToMilliseconds(minutes = minutes)

        // Then
        assertEquals(millis, 60000) // 1 minute = 60 seconds = 60 * 1000 milliseconds
    }

    @Test
    fun `convert Int representing seconds to milliseconds`() {
        // Given
        val seconds = 1

        // When
        val millis = DateUtils.secondsToMilliseconds(seconds = seconds)

        // Then
        assertEquals(millis, 1000) // 1 second = 1000 milliseconds
    }

    @Test
    fun `converting milliseconds to correct format for timer and stopwatch`() {
        // Given
        val millis = 1709460108645
        val millisFormatted = "01:48:645"

        // When
        val formattedTime = DateUtils.formatTimeTimerAndStopwatch(timeMillis = millis)

        // Then
        assertEquals(formattedTime, millisFormatted)
    }

    @Test
    fun `correct milliseconds from timer input values`() {
        // Given
        val hours = "2"
        val minutes = "32"
        val seconds = "1"
        val millis: Long = 9121000

        // When
        val millisAfterConversion = DateUtils.generateMillisecondsFromTimerInputValues(
            hours = hours,
            minutes = minutes,
            seconds = seconds
        )

        // Then
        assertEquals(millis, millisAfterConversion)
    }
}
