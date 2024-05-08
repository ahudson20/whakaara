package com.app.whakaara.logic

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.TimerState
import com.whakaara.data.alarm.AlarmRepository
import com.whakaara.data.datastore.PreferencesDataStoreRepository
import com.whakaara.data.preferences.PreferencesRepository
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.datastore.StopwatchDataStore
import com.whakaara.model.datastore.TimerStateDataStore
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.SettingsTime
import com.whakaara.model.preferences.TimeFormat
import com.whakaara.test.MainDispatcherRule
import com.whakaara.test.MockUtil
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class MainViewModelTest {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private var testDispatcher = UnconfinedTestDispatcher()
    private var testDispatcherMain = UnconfinedTestDispatcher()

    private lateinit var viewModel: MainViewModel
    private var repository: AlarmRepository = mockk(relaxed = true)
    private var preferencesRepository: PreferencesRepository = mockk(relaxed = true)
    private var alarmManagerWrapper: AlarmManagerWrapper = mockk(relaxed = true)
    private var timerManagerWrapper: TimerManagerWrapper = mockk(relaxed = true)
    private var stopwatchManagerWrapper: StopwatchManagerWrapper = mockk(relaxed = true)
    private var preferencesDataStore: PreferencesDataStoreRepository = mockk(relaxed = true)
    private lateinit var preferences: Preferences
    private lateinit var alarms: List<Alarm>
    private lateinit var stopwatchState: StopwatchState
    private lateinit var timerState: TimerState

    @Before
    fun setUp() {
        stopwatchState = StopwatchState()
        timerState = TimerState()

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
        preferences = MockUtil.mockPreferences()

        coEvery { repository.getAllAlarmsFlow() } returns flowOf(alarms)
        coEvery { repository.insert(any()) } just Runs
        coEvery { repository.delete(any()) } just Runs
        coEvery { repository.update(any()) } just Runs

        coEvery { preferencesRepository.getPreferencesFlow() } returns flowOf(preferences)
        coEvery { preferencesRepository.updatePreferences(any()) } just Runs

        every { alarmManagerWrapper.createAlarm(any(), any(), any(), any(), any(), any(), any()) } just Runs
        every { alarmManagerWrapper.stopStartUpdateWidget(any(), any(), any(), any(), any(), any(), any()) } just Runs
        every { alarmManagerWrapper.deleteAlarm(any()) } just Runs
        every { alarmManagerWrapper.setUpcomingAlarm(any(), any(), any(), any(), any(), any()) } just Runs
        every { alarmManagerWrapper.updateWidget() } just Runs
        every { alarmManagerWrapper.cancelUpcomingAlarm(any(), any()) } just Runs

        every { stopwatchManagerWrapper.startStopwatch() } just Runs
        every { stopwatchManagerWrapper.pauseStopwatch() } just Runs
        every { stopwatchManagerWrapper.resetStopwatch() } just Runs
        every { stopwatchManagerWrapper.lapStopwatch() } just Runs
        every { stopwatchManagerWrapper.createStopwatchNotification() } just Runs
        every { stopwatchManagerWrapper.pauseStopwatchNotification() } just Runs
        every { stopwatchManagerWrapper.cancelNotification() } just Runs

        coEvery { preferencesDataStore.saveStopwatchState(any()) } just Runs
        coEvery { preferencesDataStore.clearStopwatchState() } just Runs
        coEvery { preferencesDataStore.saveTimerData(any()) } just Runs

        every { timerManagerWrapper.updateInputHours(any()) } just Runs
        every { timerManagerWrapper.updateInputMinutes(any()) } just Runs
        every { timerManagerWrapper.updateInputSeconds(any()) } just Runs
        every { timerManagerWrapper.startTimer() } just Runs
        every { timerManagerWrapper.pauseTimer() } just Runs
        every { timerManagerWrapper.restartTimer(any()) } just Runs
        every { timerManagerWrapper.recreateActiveTimer(any()) } just Runs
        every { timerManagerWrapper.recreatePausedTimer(any()) } just Runs
        every { timerManagerWrapper.pauseTimerNotificationCountdown() } just Runs
        every { timerManagerWrapper.startTimerNotificationCountdown(any()) } just Runs
        every { timerManagerWrapper.cancelNotification() } just Runs

        viewModel = MainViewModel(
            repository,
            preferencesRepository,
            alarmManagerWrapper,
            timerManagerWrapper,
            stopwatchManagerWrapper,
            preferencesDataStore,
            testDispatcher,
            testDispatcherMain
        )
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
        val preferences = MockUtil.mockPreferences().apply {
            autoSilenceTime = SettingsTime.FIVE
            snoozeTime = SettingsTime.ONE
        }
        val preferencesSlot = slot<Preferences>()

        // When
        viewModel.updatePreferences(preferences = preferences)

        // Then
        coVerify(exactly = 1) { preferencesRepository.updatePreferences(capture(preferencesSlot)) }
        assertEquals(SettingsTime.FIVE, preferencesSlot.captured.autoSilenceTime)
        assertEquals(SettingsTime.ONE, preferencesSlot.captured.snoozeTime)
    }

    //region alarm
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
        coVerify(exactly = 1) {
            alarmManagerWrapper.createAlarm(
                alarm.alarmId.toString(),
                alarm.date,
                any(),
                any(),
                any(),
                alarm.repeatDaily,
                alarm.daysOfWeek
            )
        }
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
        coVerify(exactly = 1) {
            alarmManagerWrapper.stopStartUpdateWidget(
                alarmId = alarm.alarmId.toString(),
                date = alarm.date,
                any(),
                any(),
                any(),
                alarm.repeatDaily,
                alarm.daysOfWeek
            )
        }
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
        coVerify(exactly = 1) {
            alarmManagerWrapper.stopStartUpdateWidget(
                alarmId = capture(alarmIdSlot),
                date = alarm.date,
                any(),
                any(),
                any(),
                alarm.repeatDaily,
                alarm.daysOfWeek
            )
        }
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
        coVerify(exactly = 1) {
            alarmManagerWrapper.stopStartUpdateWidget(
                alarmId = capture(alarmIdSlot),
                date = any(),
                any(),
                any(),
                any(),
                alarm.repeatDaily,
                alarm.daysOfWeek
            )
        }
        assertEquals(alarm.alarmId.toString(), alarmIdSlot.captured)
    }

    @Test
    fun `update all alarm subtitles 12 hour format`() = runTest {
        // Given
        val alarmSlots = mutableListOf<Alarm>()

        // When
        viewModel.updateAllAlarmSubtitles(format = TimeFormat.TWELVE_HOURS)

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

        // When
        viewModel.updateAllAlarmSubtitles(format = TimeFormat.TWENTY_FOUR_HOURS)

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
        coVerify(exactly = 2) {
            alarmManagerWrapper.setUpcomingAlarm(
                capture(alarmIdSlots),
                any(),
                capture(upcomingAlarmNotificationEnabledSlots),
                any(),
                any(),
                any()
            )
        }
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

    @Test
    fun `get initial time to alarm`() = runTest {
        // Given
        val booleanSlot = slot<Boolean>()
        val timeSlot = slot<Calendar>()
        val isEnabled = true
        val time = Calendar.getInstance()

        // When
        viewModel.getInitialTimeToAlarm(isEnabled = isEnabled, time = time)

        // Then
        verify(exactly = 1) { alarmManagerWrapper.getInitialTimeToAlarm(capture(booleanSlot), capture(timeSlot)) }
        assertEquals(isEnabled, booleanSlot.captured)
        assertEquals(time, timeSlot.captured)
    }

    @Test
    fun `get time until alarm formatted`() = runTest {
        // Given
        val date = Calendar.getInstance()
        val dateSlot = slot<Calendar>()

        // When
        viewModel.getTimeUntilAlarmFormatted(date = date)

        // Then
        verify(exactly = 1) { alarmManagerWrapper.getTimeUntilAlarmFormatted(capture(dateSlot)) }
        assertEquals(date, dateSlot.captured)
    }
    //endregion

    //region stopwatch
    @Test
    fun `start stopwatch`() = runTest {
        // Given + When
        viewModel.startStopwatch()

        // Then
        verify(exactly = 1) { stopwatchManagerWrapper.startStopwatch() }
    }

    @Test
    fun `pause stopwatch`() = runTest {
        // Given + When
        viewModel.pauseStopwatch()

        // Then
        verify(exactly = 1) { stopwatchManagerWrapper.pauseStopwatch() }
    }

    @Test
    fun `reset stopwatch`() = runTest {
        // Given + When
        viewModel.resetStopwatch()

        // Then
        verify(exactly = 1) { stopwatchManagerWrapper.resetStopwatch() }
        coVerify(exactly = 1) { preferencesDataStore.clearStopwatchState() }
    }

    @Test
    fun `lap stopwatch`() = runTest {
        // Given + When
        viewModel.lapStopwatch()

        // Then
        verify(exactly = 1) { stopwatchManagerWrapper.lapStopwatch() }
    }

    @Test
    fun `save stopwatch state for recreation - not active or paused`() = runTest {
        // Given
        every { stopwatchManagerWrapper.stopwatchState.value } returns stopwatchState

        // When
        viewModel.saveStopwatchStateForRecreation()

        // Then
        coVerify(exactly = 0) { preferencesDataStore.saveStopwatchState(any()) }
    }

    @Test
    fun `save stopwatch state for recreation - active state`() = runTest {
        // Given
        every { stopwatchManagerWrapper.stopwatchState.value } returns stopwatchState.copy(isActive = true, timeMillis = 123L)
        val stopwatchStateSlot = slot<StopwatchDataStore>()

        // When
        viewModel.saveStopwatchStateForRecreation()

        // Then
        coVerify(exactly = 1) { preferencesDataStore.saveStopwatchState(capture(stopwatchStateSlot)) }
        with(stopwatchStateSlot.captured) {
            assertEquals(true, isActive)
            assertEquals(123L, timeMillis)
        }
    }

    @Test
    fun `save stopwatch state for recreation - paused state`() = runTest {
        // Given
        every { stopwatchManagerWrapper.stopwatchState.value } returns stopwatchState.copy(isPaused = true, timeMillis = 123L)
        val stopwatchStateSlot = slot<StopwatchDataStore>()

        // When
        viewModel.saveStopwatchStateForRecreation()

        // Then
        coVerify(exactly = 1) { preferencesDataStore.saveStopwatchState(capture(stopwatchStateSlot)) }
        with(stopwatchStateSlot.captured) {
            assertEquals(true, isPaused)
            assertEquals(123L, timeMillis)
        }
    }

    @Test
    fun `recreate stopwatch state - not default state`() = runTest {
        // Given
        every { stopwatchManagerWrapper.stopwatchState.value } returns stopwatchState.copy(isPaused = true, timeMillis = 123L)

        // When
        viewModel.recreateStopwatch()

        // Then
        verify(exactly = 0) { stopwatchManagerWrapper.recreateStopwatchActive(any()) }
        coVerify(exactly = 0) { preferencesDataStore.clearStopwatchState() }
    }

    @Test
    fun `recreate stopwatch state - default state, preferences state is active`() = runTest {
        // Given
        every { stopwatchManagerWrapper.stopwatchState.value } returns stopwatchState
        coEvery { preferencesDataStore.readStopwatchState() } returns flowOf(StopwatchDataStore(isActive = true, timeMillis = 123L))
        val stopwatchStateSlot = slot<StopwatchState>()

        // When
        viewModel.recreateStopwatch()

        // Then
        verify(exactly = 1) { stopwatchManagerWrapper.recreateStopwatchActive(capture(stopwatchStateSlot)) }
        coVerify(exactly = 1) { preferencesDataStore.clearStopwatchState() }
        with(stopwatchStateSlot.captured) {
            assertEquals(true, isActive)
            assertEquals(123L, timeMillis)
        }
    }

    @Test
    fun `recreate stopwatch state - default state, preferences state is paused`() = runTest {
        // Given
        every { stopwatchManagerWrapper.stopwatchState.value } returns stopwatchState
        coEvery { preferencesDataStore.readStopwatchState() } returns flowOf(StopwatchDataStore(isPaused = true, timeMillis = 123L))
        val stopwatchStateSlot = slot<StopwatchState>()

        // When
        viewModel.recreateStopwatch()

        // Then
        verify(exactly = 1) { stopwatchManagerWrapper.recreateStopwatchPaused(capture(stopwatchStateSlot)) }
        coVerify(exactly = 1) { preferencesDataStore.clearStopwatchState() }
        with(stopwatchStateSlot.captured) {
            assertEquals(true, isPaused)
            assertEquals(123L, timeMillis)
        }
    }

    @Test
    fun `start stopwatch notification - state is active`() = runTest {
        // Given
        every { stopwatchManagerWrapper.stopwatchState.value } returns stopwatchState.copy(isActive = true, timeMillis = 123L)

        // When
        viewModel.startStopwatchNotification()

        // Then
        verify(exactly = 1) { stopwatchManagerWrapper.createStopwatchNotification() }
    }

    @Test
    fun `start stopwatch notification - state is paused`() = runTest {
        // Given
        every { stopwatchManagerWrapper.stopwatchState.value } returns stopwatchState.copy(isPaused = true, timeMillis = 123L)

        // When
        viewModel.startStopwatchNotification()

        // Then
        verify(exactly = 1) { stopwatchManagerWrapper.pauseStopwatchNotification() }
    }

    @Test
    fun `cancel stopwatch notification`() = runTest {
        // Given + When
        viewModel.cancelStopwatchNotification()

        // Then
        verify(exactly = 1) { stopwatchManagerWrapper.cancelNotification() }
    }
    //endregion

    //region timer
    @Test
    fun `update input hours`() = runTest {
        // Given
        val input = "19"
        val inputSlot = slot<String>()

        // When
        viewModel.updateInputHours(newValue = input)

        // Then
        verify(exactly = 1) { timerManagerWrapper.updateInputHours(capture(inputSlot)) }
        assertEquals(input, inputSlot.captured)
    }

    @Test
    fun `update input minutes`() = runTest {
        // Given
        val input = "19"
        val inputSlot = slot<String>()

        // When
        viewModel.updateInputMinutes(newValue = input)

        // Then
        verify(exactly = 1) { timerManagerWrapper.updateInputMinutes(capture(inputSlot)) }
        assertEquals(input, inputSlot.captured)
    }

    @Test
    fun `update input seconds`() = runTest {
        // Given
        val input = "19"
        val inputSlot = slot<String>()

        // When
        viewModel.updateInputSeconds(newValue = input)

        // Then
        verify(exactly = 1) { timerManagerWrapper.updateInputSeconds(capture(inputSlot)) }
        assertEquals(input, inputSlot.captured)
    }

    @Test
    fun `start timer`() = runTest {
        // Given + When
        viewModel.startTimer()

        // Then
        verify(exactly = 1) { timerManagerWrapper.startTimer() }
    }

    @Test
    fun `pause timer`() = runTest {
        // Given + When
        viewModel.pauseTimer()

        // Then
        verify(exactly = 1) { timerManagerWrapper.pauseTimer() }
    }

    @Test
    fun `reset timer`() = runTest {
        // Given
        val timerDataStoreSlot = slot<TimerStateDataStore>()

        // When
        viewModel.resetTimer()

        // Then
        verify(exactly = 1) { timerManagerWrapper.resetTimer() }
        coVerify(exactly = 1) { preferencesDataStore.saveTimerData(capture(timerDataStoreSlot)) }
        with(timerDataStoreSlot.captured) {
            assertEquals(0L, remainingTimeInMillis)
            assertEquals(false, isActive)
            assertEquals(false, isPaused)
        }
    }

    @Test
    fun `restart timer`() = runTest {
        // Given
        val autoRestart = false
        val autoRestartSlot = slot<Boolean>()

        // When
        viewModel.restartTimer(autoRestartTimer = autoRestart)

        // Then
        verify(exactly = 1) { timerManagerWrapper.restartTimer(capture(autoRestartSlot)) }
        assertEquals(autoRestart, autoRestartSlot.captured)
    }

    @Test
    fun `recreate timer - state is not empty`() = runTest {
        // Given
        every { timerManagerWrapper.timerState.value } returns timerState.copy(currentTime = 420L)

        // When
        viewModel.recreateTimer()

        // Then
        verify(exactly = 0) { timerManagerWrapper.recreateActiveTimer(any()) }
        verify(exactly = 0) { timerManagerWrapper.recreatePausedTimer(any()) }
        coVerify(exactly = 0) { preferencesDataStore.saveTimerData(any()) }
        coVerify(exactly = 0) { preferencesDataStore.readTimerStatus() }
    }

    @Test
    fun `recreate timer - state is empty, no state saved in datastore`() = runTest {
        // Given
        every { timerManagerWrapper.timerState.value } returns timerState
        coEvery { preferencesDataStore.readTimerStatus() } returns flowOf(TimerStateDataStore())

        // When
        viewModel.recreateTimer()

        // Then
        coVerify(exactly = 1) { preferencesDataStore.readTimerStatus() }
        verify(exactly = 0) { timerManagerWrapper.recreateActiveTimer(any()) }
        verify(exactly = 0) { timerManagerWrapper.recreatePausedTimer(any()) }
        coVerify(exactly = 0) { preferencesDataStore.saveTimerData(any()) }
    }

    @Test
    fun `recreate timer - state is empty, active state saved in datastore`() = runTest {
        // Given
        every { timerManagerWrapper.timerState.value } returns timerState
        coEvery { preferencesDataStore.readTimerStatus() } returns
            flowOf(
                TimerStateDataStore(
                    remainingTimeInMillis = 420L,
                    isActive = true,
                    timeStamp = System.currentTimeMillis()
                )
            )

        // When
        viewModel.recreateTimer()

        // Then
        coVerify(exactly = 1) { preferencesDataStore.readTimerStatus() }
        verify(exactly = 1) { timerManagerWrapper.recreateActiveTimer(any()) }
        verify(exactly = 0) { timerManagerWrapper.recreatePausedTimer(any()) }
        coVerify(exactly = 1) { preferencesDataStore.saveTimerData(any()) }
    }

    @Test
    fun `recreate timer - state is empty, paused state saved in datastore`() = runTest {
        // Given
        every { timerManagerWrapper.timerState.value } returns timerState
        coEvery { preferencesDataStore.readTimerStatus() } returns
            flowOf(
                TimerStateDataStore(
                    remainingTimeInMillis = 420L,
                    isActive = false,
                    isPaused = true,
                    timeStamp = System.currentTimeMillis()
                )
            )

        // When
        viewModel.recreateTimer()

        // Then
        coVerify(exactly = 1) { preferencesDataStore.readTimerStatus() }
        verify(exactly = 0) { timerManagerWrapper.recreateActiveTimer(any()) }
        verify(exactly = 1) { timerManagerWrapper.recreatePausedTimer(any()) }
        coVerify(exactly = 1) { preferencesDataStore.saveTimerData(any()) }
    }

    @Test
    fun `save timer state for recreation - not start`() = runTest {
        // Given
        every { timerManagerWrapper.timerState.value } returns timerState.copy(isTimerActive = true, isStart = false, currentTime = 420L)
        val timerStateDataStoreSlot = slot<TimerStateDataStore>()

        // When
        viewModel.saveTimerStateForRecreation()

        // Then
        coVerify(exactly = 1) { preferencesDataStore.saveTimerData(capture(timerStateDataStoreSlot)) }
        with(timerStateDataStoreSlot.captured) {
            assertEquals(420L, remainingTimeInMillis)
            assertEquals(true, isActive)
            assertEquals(false, isPaused)
        }
    }

    @Test
    fun `save timer state for recreation - is start`() = runTest {
        // Given
        every { timerManagerWrapper.timerState.value } returns timerState.copy(isStart = true)

        // When
        viewModel.saveTimerStateForRecreation()

        // Then
        coVerify(exactly = 0) { preferencesDataStore.saveTimerData(any()) }
    }

    @Test
    fun `start timer notification - timer active`() = runTest {
        // Given
        every { timerManagerWrapper.timerState.value } returns timerState.copy(isTimerActive = true)

        // When
        viewModel.startTimerNotification()

        // Then
        verify(exactly = 1) { timerManagerWrapper.startTimerNotificationCountdown(any()) }
    }

    @Test
    fun `start timer notification - timer paused`() = runTest {
        // Given
        every { timerManagerWrapper.timerState.value } returns timerState.copy(isTimerPaused = true)

        // When
        viewModel.startTimerNotification()

        // Then
        verify { timerManagerWrapper.pauseTimerNotificationCountdown() }
    }

    @Test
    fun `cancel timer notification`() = runTest {
        // Given + When
        viewModel.cancelTimerNotification()

        // Then
        verify(exactly = 1) { timerManagerWrapper.cancelNotification() }
    }
    //endregion
}
