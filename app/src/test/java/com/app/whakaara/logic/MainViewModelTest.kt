package com.app.whakaara.logic

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.app.whakaara.MainDispatcherRule
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.datastore.PreferencesDataStore
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.PreferencesRepository
import com.app.whakaara.data.preferences.SettingsTime
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.TimerState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Calendar

@RunWith(JUnit4::class)
class MainViewModelTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MainViewModel
    private var repository: AlarmRepository = mockk(relaxed = true)
    private var preferencesRepository: PreferencesRepository = mockk(relaxed = true)
    private var alarmManagerWrapper: AlarmManagerWrapper = mockk(relaxed = true)
    private var timerManagerWrapper: TimerManagerWrapper = mockk(relaxed = true)
    private var stopwatchManagerWrapper: StopwatchManagerWrapper = mockk(relaxed = true)
    private var preferencesDataStore: PreferencesDataStore = mockk(relaxed = true)
    private lateinit var preferences: Preferences
    private lateinit var alarms: List<Alarm>
    private lateinit var stopwatchState: StopwatchState
    private lateinit var timerState: TimerState

    @Before
    fun setUp() {
        stopwatchState = StopwatchState()
        timerState = TimerState()

        every { stopwatchManagerWrapper.stopwatchState } returns MutableStateFlow(stopwatchState)
        every { timerManagerWrapper.timerState } returns MutableStateFlow(timerState)
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

        coEvery { repository.getAllAlarmsFlow() } returns flowOf(alarms)
        coEvery { repository.insert(any()) } returns Unit
        coEvery { repository.delete(any()) } returns Unit
        coEvery { repository.update(any()) } returns Unit

        coEvery { preferencesRepository.getPreferencesFlow() } returns flowOf(preferences)
        coEvery { preferencesRepository.updatePreferences(any()) } returns Unit

        coEvery { alarmManagerWrapper.createAlarm(any(), any(), any(), any(), any(), any(), any()) } returns Unit
        coEvery { alarmManagerWrapper.stopStartUpdateWidget(any(), any(), any(), any(), any(), any(), any()) } returns Unit
        coEvery { alarmManagerWrapper.deleteAlarm(any()) } returns Unit
        coEvery { alarmManagerWrapper.setUpcomingAlarm(any(), any(), any(), any(), any(), any()) } returns Unit
        coEvery { alarmManagerWrapper.updateWidget() } returns Unit
        coEvery { alarmManagerWrapper.cancelUpcomingAlarm(any(), any()) } returns Unit

        viewModel = MainViewModel(repository, preferencesRepository, alarmManagerWrapper, timerManagerWrapper, stopwatchManagerWrapper, preferencesDataStore)
    }

    @Test
    fun `init test - alarm state`() = runTest {
        // Given + When + Then
        viewModel.alarmState.test {
            val alarmState = awaitItem()

            assertTrue(alarmState is AlarmState.Success)

            with(alarmState as AlarmState.Success) {
                assertEquals(2, this.alarms.size)

                this.alarms[0].apply {
                    assertEquals("First Alarm Title", this.title)
                    assertEquals("14:34 PM", this.subTitle)
                }

                this.alarms[1].apply {
                    assertEquals("Second Alarm Title", this.title)
                    assertEquals("14:34 PM", this.subTitle)
                }
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
                assertEquals(SettingsTime.TEN, this.preferences.autoSilenceTime)
                assertEquals(SettingsTime.TEN, this.preferences.snoozeTime)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `update preferences`() = runTest {
        // Given
        val preferences = Preferences(
            autoSilenceTime = SettingsTime.TEN,
            snoozeTime = SettingsTime.FIFTEEN
        )
        val preferencesSlot = slot<Preferences>()

        // When
        viewModel.updatePreferences(preferences = preferences)

        // Then
        coVerify(exactly = 1) { preferencesRepository.updatePreferences(capture(preferencesSlot)) }
        assertEquals(SettingsTime.TEN, preferencesSlot.captured.autoSilenceTime)
        assertEquals(SettingsTime.FIFTEEN, preferencesSlot.captured.snoozeTime)
    }

    @Test
    fun `create alarm`() = runTest {
        // Given
        val alarm = Alarm(
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
            },
            subTitle = "10:03PM"
        )
        val alarmSlot = slot<Alarm>()

        // When
        viewModel.create(alarm = alarm)

        // Then
        coVerify(exactly = 1) { repository.insert(capture(alarmSlot)) }
        coVerify(exactly = 1) { alarmManagerWrapper.createAlarm(alarm.alarmId.toString(), alarm.date, any(), any(), any(), alarm.repeatDaily, alarm.daysOfWeek) }
        with(alarmSlot.captured) {
            assertEquals(alarm.date, date)
            assertEquals("10:03PM", subTitle)
        }
    }

    @Test
    fun `delete alarm`() = runTest {
        // Given
        val alarm = Alarm(
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
            },
            subTitle = "10:03PM"
        )
        val alarmSlot = slot<Alarm>()
        val alarmIdSlot = slot<String>()

        // When
        viewModel.delete(alarm = alarm)

        // Then
        coVerify(exactly = 1) { repository.delete(capture(alarmSlot)) }
        coVerify(exactly = 1) { alarmManagerWrapper.deleteAlarm(capture(alarmIdSlot)) }
        coVerify(exactly = 1) { alarmManagerWrapper.cancelUpcomingAlarm(alarmId = any(), alarmDate = any()) }
        with(alarmSlot.captured) {
            assertEquals(alarm.date, date)
            assertEquals("10:03PM", subTitle)
        }
        assertEquals(alarm.alarmId.toString(), alarmIdSlot.captured)
    }

    @Test
    fun `disable alarm`() = runTest {
        // Given
        val alarm = Alarm(
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
            },
            subTitle = "10:03PM"
        )
        val alarmSlot = slot<Alarm>()
        val alarmIdSlot = slot<String>()

        // When
        viewModel.disable(alarm = alarm)

        // Then
        coVerify(exactly = 1) { repository.update(capture(alarmSlot)) }
        coVerify(exactly = 1) { alarmManagerWrapper.deleteAlarm(alarmId = capture(alarmIdSlot)) }
        coVerify(exactly = 1) { alarmManagerWrapper.cancelUpcomingAlarm(alarmId = any(), alarmDate = any()) }
        with(alarmSlot.captured) {
            assertEquals(alarm.date, date)
            assertEquals("10:03PM", subTitle)
            assertEquals(false, isEnabled)
        }
        assertEquals(alarm.alarmId.toString(), alarmIdSlot.captured)
    }

    @Test
    fun `enable alarm`() = runTest {
        // Given
        val alarm = Alarm(
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
            },
            subTitle = "10:03PM"
        )
        val alarmSlot = slot<Alarm>()

        // When
        viewModel.enable(alarm = alarm)

        // Then
        coVerify(exactly = 1) { repository.update(capture(alarmSlot)) }
        coVerify(exactly = 1) { alarmManagerWrapper.stopStartUpdateWidget(alarmId = alarm.alarmId.toString(), date = alarm.date, any(), any(), any(), alarm.repeatDaily, alarm.daysOfWeek) }
        with(alarmSlot.captured) {
            assertEquals(alarm.date, date)
            assertEquals("10:03PM", subTitle)
            assertEquals(true, isEnabled)
        }
    }

    @Test
    fun `reset alarm`() = runTest {
        // Given
        val alarm = Alarm(
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
            },
            subTitle = "10:03PM"
        )
        val alarmSlot = slot<Alarm>()
        val alarmIdSlot = slot<String>()

        // When
        viewModel.reset(alarm = alarm)

        // Then
        coVerify(exactly = 1) { repository.update(capture(alarmSlot)) }
        coVerify(exactly = 1) { alarmManagerWrapper.stopStartUpdateWidget(alarmId = capture(alarmIdSlot), date = alarm.date, any(), any(), any(), alarm.repeatDaily, alarm.daysOfWeek) }
        with(alarmSlot.captured) {
            assertEquals(alarm.date, date)
            assertEquals("10:03PM", subTitle)
            assertEquals(true, isEnabled)
        }
        assertEquals(alarm.alarmId.toString(), alarmIdSlot.captured)
    }

    @Test
    fun `snooze alarm`() = runTest {
        // Given
        val alarm = Alarm(
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
            },
            subTitle = "10:03PM"
        )
        val alarmIdSlot = slot<String>()

        // When
        viewModel.snooze(alarm = alarm)

        // Then
        coVerify(exactly = 1) { alarmManagerWrapper.stopStartUpdateWidget(alarmId = capture(alarmIdSlot), date = any(), any(), any(), any(), alarm.repeatDaily, alarm.daysOfWeek) }
        assertEquals(alarm.alarmId.toString(), alarmIdSlot.captured)
    }

    @Test
    fun `update all alarm subtitles 12 hour format`() = runTest {
        // Given
        val alarmSlots = mutableListOf<Alarm>()
//        alarms.forEach {
//            assertEquals("14:34 PM", it.subTitle)
//        }

        // When
        viewModel.updateAllAlarmSubtitles(format = false)

        // Then
        coVerify(exactly = 2) { repository.update(capture(alarmSlots)) }
        coVerify(exactly = 1) { alarmManagerWrapper.updateWidget() }
        alarmSlots.forEach {
            assertEquals("2:34 PM", it.subTitle)
        }
    }

    @Test
    fun `update all alarm subtitles 24 hour format`() = runTest {
        // Given
        val alarmSlots = mutableListOf<Alarm>()
        alarms.forEach {
            assertEquals("14:34 PM", it.subTitle)
        }

        // When
        viewModel.updateAllAlarmSubtitles(format = true)

        // Then
        coVerify(exactly = 2) { repository.update(capture(alarmSlots)) }
        coVerify(exactly = 1) { alarmManagerWrapper.updateWidget() }
        alarmSlots.forEach {
            assertEquals("14:34", it.subTitle)
        }
    }

    @Test
    fun `update current alarms to add upcoming alarm notification`() = runTest {
        // Given
        val alarmIdSlots = mutableListOf<String>()
        val upcomingAlarmNotificationEnabledSlots = mutableListOf<Boolean>()

        // When
        viewModel.updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(true)

        // Then
        coVerify(exactly = 2) { alarmManagerWrapper.setUpcomingAlarm(capture(alarmIdSlots), any(), capture(upcomingAlarmNotificationEnabledSlots), any(), any(), any()) }
        assertEquals(2, alarmIdSlots.size)
        assertEquals(alarms.first().alarmId.toString(), alarmIdSlots.first())
        assertEquals(alarms.last().alarmId.toString(), alarmIdSlots.last())
        assertEquals(2, upcomingAlarmNotificationEnabledSlots.size)
        upcomingAlarmNotificationEnabledSlots.forEach {
            assertEquals(true, it)
        }
    }

    @Test
    fun `update current alarms to remove upcoming alarm notification`() = runTest {
        // Given
        val alarmIdSlots = mutableListOf<String>()

        // When
        viewModel.updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(false)

        // Then
        coVerify(exactly = 2) { alarmManagerWrapper.cancelUpcomingAlarm(capture(alarmIdSlots), any()) }
        assertEquals(2, alarmIdSlots.size)
        assertEquals(alarms.first().alarmId.toString(), alarmIdSlots.first())
        assertEquals(alarms.last().alarmId.toString(), alarmIdSlots.last())
    }
}
