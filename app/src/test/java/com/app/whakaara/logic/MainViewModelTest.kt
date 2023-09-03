package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.PreferencesRepository
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Calendar
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class MainViewModelTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var app: Application
    private lateinit var viewModel: MainViewModel
    private lateinit var repository: AlarmRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var preferences: Preferences
    private lateinit var alarms: List<Alarm>
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var intent: Intent

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        preferencesRepository = mockk()
        app = mockk()
        alarmManager = mockk()
        pendingIntent = mockk()
        intent = mockk()

        viewModel = MainViewModel(app, repository, preferencesRepository)

        alarms = listOf(
            Alarm(
                date = Calendar.getInstance().apply {
                    set(Calendar.YEAR, 2023)
                    set(Calendar.DAY_OF_MONTH, 13)
                    set(Calendar.MONTH, 6)
                    set(Calendar.HOUR_OF_DAY, 14)
                    set(Calendar.MINUTE, 34)
                    set(Calendar.SECOND, 0)
                },
                title = "First Alarm Title",
                subTitle = "14:34 PM"
            ),
            Alarm(
                date = Calendar.getInstance().apply {
                    set(Calendar.YEAR, 2023)
                    set(Calendar.DAY_OF_MONTH, 14)
                    set(Calendar.MONTH, 7)
                    set(Calendar.HOUR_OF_DAY, 14)
                    set(Calendar.MINUTE, 34)
                    set(Calendar.SECOND, 0)
                },
                title = "Second Alarm Title",
                subTitle = "14:34 PM"
            )
        )
        preferences = Preferences()

        mockkConstructor(Intent::class)

        mockkObject(PendingIntentUtils.Companion)
        every { PendingIntentUtils.getBroadcast(any(), any(), any(), any()) } returns pendingIntent

        mockkObject(GeneralUtils.Companion)
        every { GeneralUtils.convertAlarmObjectToString(any()) } returns "alarmString"

        every { app.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        every { alarmManager.cancel(pendingIntent) } returns Unit

        coEvery { repository.getAllAlarmsFlow() } returns flowOf(alarms)
        coEvery { repository.update(any()) } returns Unit
        coEvery { preferencesRepository.getPreferencesFlow() } returns flowOf(preferences)
        coEvery { preferencesRepository.updatePreferences(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `init test - alarm state`() = runTest {
        // Given + When + Then
        viewModel.uiState.test {
            val alarmList = awaitItem()
            assertEquals(2, alarmList.alarms.size)

            alarmList.alarms[0].apply {
                assertEquals("First Alarm Title", this.title)
                assertEquals("14:34 PM", this.subTitle)
            }

            alarmList.alarms[1].apply {
                assertEquals("Second Alarm Title", this.title)
                assertEquals("14:34 PM", this.subTitle)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init test - preferences state`() = runTest {
        // Given + When + Then
        viewModel.preferencesUiState.test {
            val pref = awaitItem()
            pref.apply {
                assertEquals(true, this.preferences.isVibrateEnabled)
                assertEquals(true, this.preferences.isSnoozeEnabled)
                assertEquals(false, this.preferences.deleteAfterGoesOff)
                assertEquals(10, this.preferences.autoSilenceTime)
                assertEquals(10, this.preferences.snoozeTime)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updatePreferences - verify repository update called, with correct values`() = runTest {
        // Given
        val pref = Preferences(
            autoSilenceTime = 1234,
            snoozeTime = 5678
        )
        val prefSlot = slot<Preferences>()

        // When
        viewModel.updatePreferences(preferences = pref)

        // Then
        coVerify { preferencesRepository.updatePreferences(capture(prefSlot)) }
        assertEquals(1234, prefSlot.captured.autoSilenceTime)
        assertEquals(5678, prefSlot.captured.snoozeTime)
    }

    @Test
    fun `disable alarm`() = runTest {
        // Given
        val alarm = Alarm(
            alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"),
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
            },
            isEnabled = true,
            isSnoozeEnabled = false,
            title = "Alarm Title",
            subTitle = "First SubTitle"
        )
        val alarmSlot = slot<Alarm>()

        every {
            anyConstructed<Intent>().setAction("19de4fcc-1c68-485c-b817-0290faec649d")
        } returns intent

        every {
            anyConstructed<Intent>().putExtra(NotificationUtilsConstants.INTENT_EXTRA_ALARM, "alarmString")
        } returns intent

        // When
        viewModel.disable(alarm)

        // Then
        coVerify { repository.update(capture(alarmSlot)) }
        assertEquals(false, alarmSlot.captured.isEnabled)
        verify { alarmManager.cancel(pendingIntent) }
    }
}
