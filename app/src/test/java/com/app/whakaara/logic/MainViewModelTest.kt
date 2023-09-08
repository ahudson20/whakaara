package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.util.Calendar
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
class MainViewModelTest {

    private var openMocks: AutoCloseable? = null

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var app: Application

    @Mock
    lateinit var repository: AlarmRepository

    @Mock
    lateinit var preferencesRepository: PreferencesRepository

    @Mock
    lateinit var alarmManager: AlarmManager

    private lateinit var viewModel: MainViewModel
    private lateinit var alarms: List<Alarm>
    private lateinit var preferences: Preferences

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        openMocks = MockitoAnnotations.openMocks(this)

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

        whenever(app.getSystemService(any())).thenReturn(alarmManager)

        whenever(repository.getAllAlarmsFlow()).thenReturn(flowOf(alarms))
        whenever(preferencesRepository.getPreferencesFlow()).thenReturn(flowOf(preferences))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getUiState() {
    }

    @Test
    fun getPreferencesUiState() {
    }

    @Test
    fun updatePreferences() = runTest {
        // Given
        val pref = Preferences(
            autoSilenceTime = 1234,
            snoozeTime = 5678
        )

        // When
        viewModel.updatePreferences(preferences = pref)

        // Then
        verify(preferencesRepository, atLeastOnce()).updatePreferences(pref)
    }

    @Test
    fun create() = runTest {
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

        // When
        viewModel.create(alarm = alarm)

        // Then
        verify(repository, atLeastOnce()).insert(alarm)
    }

    @Test
    fun delete() {
    }

    @Test
    fun disable() {
    }

    @Test
    fun enable() {
    }

    @Test
    fun reset() {
    }

    @Test
    fun snooze() {
    }
}
