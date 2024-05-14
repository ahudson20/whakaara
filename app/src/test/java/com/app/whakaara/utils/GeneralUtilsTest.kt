package com.app.whakaara.utils

import androidx.compose.ui.graphics.Color
import com.app.whakaara.utility.GeneralUtils
import com.app.whakaara.utility.GeneralUtils.Companion.toColorInt
import com.whakaara.model.alarm.Alarm
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.UUID

class GeneralUtilsTest {
    @Test
    fun `convert Alarm to String`() {
        // Given
        val alarm = Alarm(
            alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"),
            subTitle = "subTitle",
            date = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2023)
                set(Calendar.DAY_OF_MONTH, 13)
                set(Calendar.MONTH, 6)
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
                set(Calendar.SECOND, 0)
            }
        )

        // When
        val alarmString = GeneralUtils.convertAlarmObjectToString(alarm = alarm)

        // Then
        assertEquals(
            "{\"alarmId\":\"19de4fcc-1c68-485c-b817-0290faec649d\",\"date\":{\"year\":2023,\"month\":6,\"dayOfMonth\":13,\"hourOfDay\":12,\"minute\":34,\"second\":0},\"title\":\"Alarm\",\"subTitle\":\"subTitle\",\"vibration\":true,\"isEnabled\":true,\"isSnoozeEnabled\":true,\"deleteAfterGoesOff\":false,\"repeatDaily\":false,\"daysOfWeek\":[]}",
            alarmString
        )
    }

    @Test
    fun `convert String to Alarm`() {
        // Given
        val alarmString = "{\"alarmId\":\"19de4fcc-1c68-485c-b817-0290faec649d\",\"date\":{\"year\":2023,\"month\":6,\"dayOfMonth\":13,\"hourOfDay\":12,\"minute\":34,\"second\":0},\"title\":\"Alarm\",\"subTitle\":\"subTitle\",\"vibration\":true,\"isEnabled\":true,\"isSnoozeEnabled\":true,\"deleteAfterGoesOff\":false,\"repeatDaily\":false,\"daysOfWeek\":[]}"
        // When
        val alarmFromString = GeneralUtils.convertStringToAlarmObject(string = alarmString)

        // Then
        alarmFromString.apply {
            assertEquals(UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"), this.alarmId)
            assertEquals("Alarm", this.title)
            assertEquals("subTitle", this.subTitle)
            assertEquals(true, this.vibration)
            assertEquals(true, this.isEnabled)
            assertEquals(true, this.isSnoozeEnabled)
            assertEquals(false, this.deleteAfterGoesOff)
        }
    }

    @Test
    fun `convert string to colour`() {
        // Given
        val string = "{\"value\":-4294967296}"

        // When
        val colour = GeneralUtils.convertStringToColour(string = string)

        // Then
        assertEquals(Color.White, colour)
    }

    @Test
    fun `convert float to colour int`() {
        // Given
        val float = 123.1F

        // When
        val value = float.toColorInt()

        // Then
        assertEquals(value, 31391)
    }
}
