package com.whakaara.database

import com.whakaara.database.preferences.converters.PreferencesConverter
import com.whakaara.model.preferences.AppTheme
import com.whakaara.model.preferences.GradualSoundDuration
import com.whakaara.model.preferences.SettingsTime
import com.whakaara.model.preferences.TimeFormat
import com.whakaara.model.preferences.VibrationPattern
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PreferencesConverterTest {
    private lateinit var preferencesConverter: PreferencesConverter

    @Before
    fun setUp() {
        preferencesConverter = PreferencesConverter()
    }

    @Test
    fun `to vibration pattern`() {
        val pattern = preferencesConverter.toVibrationPattern(1)
        assertEquals(VibrationPattern.DOUBLE, pattern)
        assertEquals(1, pattern.value)
    }

    @Test
    fun `from vibration pattern`() {
        val int = preferencesConverter.fromVibrationPattern(VibrationPattern.CLICK)
        assertEquals(0, int)
    }

    @Test
    fun `to time`() {
        val time = preferencesConverter.toTime(1)
        assertEquals(SettingsTime.FIVE, time)
        assertEquals(5, time.value)
    }

    @Test
    fun `from time`() {
        val int = preferencesConverter.fromTime(SettingsTime.FIVE)
        assertEquals(1, int)
    }

    @Test
    fun `to app theme`() {
        val theme = preferencesConverter.toAppTheme(2)
        assertEquals(AppTheme.MODE_AUTO, theme)
        assertEquals("System preference", theme.label)
    }

    @Test
    fun `from app theme`() {
        val int = preferencesConverter.fromAppTheme(AppTheme.MODE_DAY)
        assertEquals(0, int)
    }

    @Test
    fun `to time format`() {
        val timeFormat = preferencesConverter.toTimeFormat(1)
        assertEquals(TimeFormat.TWELVE_HOURS, timeFormat)
    }

    @Test
    fun `from time format`() {
        val int = preferencesConverter.fromTimeFormat(TimeFormat.TWENTY_FOUR_HOURS)
        assertEquals(0, int)
    }

    @Test
    fun `to gradual sound duration`() {
        val gradualSoundDuration = preferencesConverter.toGradualSoundDuration(1)
        assertEquals(GradualSoundDuration.GRADUAL_INCREASE_DURATION_10_SECONDS, gradualSoundDuration)
        assertEquals(10, gradualSoundDuration.seconds)
        assertEquals(10000, gradualSoundDuration.inMillis())
        assertEquals("10", gradualSoundDuration.toString())
    }

    @Test
    fun `from gradual sound duration`() {
        val int = preferencesConverter.fromGradualSoundDuration(GradualSoundDuration.GRADUAL_INCREASE_DURATION_20_SECONDS)
        assertEquals(2, int)
    }
}
