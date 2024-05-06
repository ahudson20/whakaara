package com.whakaara.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.whakaara.data.preferences.PreferencesImpl
import com.whakaara.data.preferences.PreferencesRepository
import com.whakaara.database.preferences.PreferencesDao
import com.whakaara.database.preferences.entity.PreferencesEntity
import com.whakaara.database.preferences.entity.asExternalModel
import com.whakaara.model.preferences.AppTheme
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.SettingsTime
import com.whakaara.model.preferences.VibrationPattern
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PreferencesEntityImplTest {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var repository: PreferencesRepository
    private lateinit var preferencesDao: PreferencesDao

    @Before
    fun setUp() {
        preferencesDao = mockk()
        repository = PreferencesImpl(preferencesDao = preferencesDao)
    }

    @Test
    fun `get preferences flow`() =
        runTest {
            // Given
            val preferences =
                PreferencesEntity(
                    id = 1,
                    is24HourFormat = false,
                    isVibrateEnabled = false,
                    isSnoozeEnabled = false,
                    deleteAfterGoesOff = false,
                    autoSilenceTime = SettingsTime.FIFTEEN,
                    snoozeTime = SettingsTime.FIFTEEN,
                    alarmSoundPath = "",
                    vibrationPattern = VibrationPattern.CLICK,
                    appTheme = AppTheme.MODE_AUTO,
                    shouldShowOnboarding = false,
                    isVibrationTimerEnabled = false,
                    timerVibrationPattern = VibrationPattern.CLICK,
                    filteredAlarmList = false,
                    upcomingAlarmNotification = false,
                    upcomingAlarmNotificationTime = SettingsTime.FIFTEEN,
                    dynamicTheme = false,
                    autoRestartTimer = true,
                    timerSoundPath = ""
                )
            coEvery { preferencesDao.getPreferencesFlow() } returns flowOf(preferences)

            // When
            repository.getPreferencesFlow().test {
                // Then
                assertEquals(preferences.asExternalModel(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `insert preferences`() =
        runTest {
            // Given
            val preferences =
                Preferences(
                    id = 1,
                    is24HourFormat = false,
                    isVibrateEnabled = false,
                    isSnoozeEnabled = false,
                    deleteAfterGoesOff = false,
                    autoSilenceTime = SettingsTime.FIFTEEN,
                    snoozeTime = SettingsTime.FIFTEEN,
                    alarmSoundPath = "",
                    vibrationPattern = VibrationPattern.CLICK,
                    appTheme = AppTheme.MODE_AUTO,
                    shouldShowOnboarding = false,
                    isVibrationTimerEnabled = false,
                    timerVibrationPattern = VibrationPattern.CLICK,
                    filteredAlarmList = false,
                    upcomingAlarmNotification = false,
                    upcomingAlarmNotificationTime = SettingsTime.FIFTEEN,
                    dynamicTheme = false,
                    autoRestartTimer = true,
                    timerSoundPath = ""
                )
            val preferencesSlot = slot<PreferencesEntity>()
            coEvery { preferencesDao.insert(any()) } returns mockk()

            // When
            repository.insert(preferences = preferences)

            // Then
            coVerify(atLeast = 1) { preferencesDao.insert(capture(preferencesSlot)) }
            with(preferencesSlot.captured) {
                assertEquals(15, autoSilenceTime.value)
                assertEquals(15, snoozeTime.value)
            }
        }

    @Test
    fun `update preferences`() =
        runTest {
            // Given
            val preferences =
                Preferences(
                    id = 1,
                    is24HourFormat = false,
                    isVibrateEnabled = false,
                    isSnoozeEnabled = false,
                    deleteAfterGoesOff = false,
                    autoSilenceTime = SettingsTime.FIFTEEN,
                    snoozeTime = SettingsTime.FIFTEEN,
                    alarmSoundPath = "",
                    vibrationPattern = VibrationPattern.CLICK,
                    appTheme = AppTheme.MODE_AUTO,
                    shouldShowOnboarding = false,
                    isVibrationTimerEnabled = false,
                    timerVibrationPattern = VibrationPattern.CLICK,
                    filteredAlarmList = false,
                    upcomingAlarmNotification = false,
                    upcomingAlarmNotificationTime = SettingsTime.FIFTEEN,
                    dynamicTheme = false,
                    autoRestartTimer = true,
                    timerSoundPath = ""
                )
            val preferencesSlot = slot<PreferencesEntity>()
            coEvery { preferencesDao.updatePreferences(any()) } returns mockk()

            // When
            repository.updatePreferences(preferences = preferences)

            // Then
            coVerify(atLeast = 1) { preferencesDao.updatePreferences(capture(preferencesSlot)) }
            with(preferencesSlot.captured) {
                assertEquals(15, autoSilenceTime.value)
                assertEquals(15, snoozeTime.value)
            }
        }
}
