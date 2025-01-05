package com.whakaara.feature.alarm

import com.whakaara.feature.alarm.utils.DateUtils
import com.whakaara.model.preferences.TimeFormat
import org.junit.Assert
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
        val time24HourFormat = DateUtils.getAlarmTimeFormatted(date = date, timeFormat = TimeFormat.TWENTY_FOUR_HOURS)

        // Then
        Assert.assertEquals("14:34", time24HourFormat)
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
        val time24HourFormat = DateUtils.getAlarmTimeFormatted(date = date, timeFormat = TimeFormat.TWELVE_HOURS)

        // Then
        Assert.assertEquals("2:34 PM", time24HourFormat)
    }
}
