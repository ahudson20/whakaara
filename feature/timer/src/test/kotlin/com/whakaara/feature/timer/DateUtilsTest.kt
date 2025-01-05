package com.whakaara.feature.timer

import com.whakaara.feature.timer.util.DateUtils
import com.whakaara.model.preferences.TimeFormat
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    @Test
    fun `convert time to 24 hour format`() {
        // Given
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 34)
            set(Calendar.SECOND, 0)
        }

        // When
        val time24HourFormat = DateUtils.getTimerFinishFormatted(date = date, timeFormat = TimeFormat.TWENTY_FOUR_HOURS)

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
        val time24HourFormat = DateUtils.getTimerFinishFormatted(date = date, timeFormat = TimeFormat.TWELVE_HOURS)

        // Then
        assertEquals("2:34 PM", time24HourFormat)
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
