package com.app.whakaara.logic

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.PreferencesRepository
import io.mockk.coEvery
import io.mockk.mockk
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class MainViewModelTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MainViewModel
    private lateinit var repository: AlarmRepository
    private lateinit var preferencesRepository: PreferencesRepository
    private lateinit var preferences: Preferences
    private lateinit var alarms: List<Alarm>
    private lateinit var alarmManagerWrapper: AlarmManagerWrapper

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        preferencesRepository = mockk()
        alarmManagerWrapper = mockk()

        viewModel = MainViewModel(repository, preferencesRepository, alarmManagerWrapper)

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
        coEvery { preferencesRepository.getPreferencesFlow() } returns flowOf(preferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init test - alarm state`() = runTest {
        // Given + When + Then
        viewModel.alarmState.test {
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
}
