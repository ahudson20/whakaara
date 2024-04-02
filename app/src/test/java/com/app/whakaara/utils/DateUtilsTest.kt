package com.app.whakaara.utils

import android.content.Context
import com.app.whakaara.R
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    private val mockContext = mockk<Context>(relaxed = true)

    @Before
    fun setUp() {
        every { mockContext.resources.getQuantityString(R.plurals.minutes, 0, 0) } returns "less than 1 minute"
        every { mockContext.resources.getString(R.string.time_until_alarm_formatted_prefix) } returns "Alarm in"
        every { mockContext.getString(R.string.card_alarm_sub_title_off) } returns "Off"
    }

    @Test
    fun `convert seconds to time until alarm string`() {
        // Given
        val time: Long = 111222333
        every { mockContext.resources.getQuantityString(R.plurals.minutes, 5, 5) } returns "5 minutes"
        every { mockContext.resources.getQuantityString(R.plurals.hours, 7, 7) } returns "7 hours"

        // When
        val timeString = DateUtils.convertSecondsToHMm(time, mockContext)

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
        val initial = DateUtils.getInitialTimeToAlarm(false, Calendar.getInstance(), mockContext)

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
    fun `converting milliseconds to correct format for timer`() {
        // Given
        val millis = 12312312.toLong()
        val millisFormatted = "03:25:12"

        // When
        val formattedTime = DateUtils.formatTimeForTimer(millis = millis)

        // Then
        assertEquals(formattedTime, millisFormatted)
    }

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
