package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.app.NotificationCompat
import app.cash.turbine.test
import com.app.whakaara.data.datastore.PreferencesDataStore
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class TimerManagerWrapperTest {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var timerManagerWrapper: TimerManagerWrapper
    private lateinit var app: Application
    private lateinit var alarmManager: AlarmManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var timerNotificationBuilder: NotificationCompat.Builder
    private lateinit var countDownTimerUtil: CountDownTimerUtil
    private lateinit var preferencesDatastore: PreferencesDataStore
    private lateinit var coroutineScope: CoroutineScope

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        app = mockk()
        alarmManager = mockk()
        notificationManager = mockk()
        timerNotificationBuilder = mockk()
        countDownTimerUtil = mockk()
        preferencesDatastore = mockk()
        coroutineScope = mockk()

        timerManagerWrapper = TimerManagerWrapper(app, alarmManager, notificationManager, timerNotificationBuilder, countDownTimerUtil, preferencesDatastore, coroutineScope)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `update input hours should update state`() = runTest {
        // Given
        timerManagerWrapper.updateInputHours("42")

        // When
        timerManagerWrapper.timerState.test {
            val state = awaitItem()
            // Then
            with(state) {
                assertEquals("42", inputHours)
                assertEquals("00", inputMinutes)
                assertEquals("00", inputSeconds)
            }
        }
    }

    @Test
    fun `update input minutes should update state`() = runTest {
        // Given
        timerManagerWrapper.updateInputMinutes("42")

        // When
        timerManagerWrapper.timerState.test {
            val state = awaitItem()
            // Then
            with(state) {
                assertEquals("00", inputHours)
                assertEquals("42", inputMinutes)
                assertEquals("00", inputSeconds)
            }
        }
    }

    @Test
    fun `update input seconds should update state`() = runTest {
        // Given
        timerManagerWrapper.updateInputSeconds("42")

        // When
        timerManagerWrapper.timerState.test {
            val state = awaitItem()
            // Then
            with(state) {
                assertEquals("00", inputHours)
                assertEquals("00", inputMinutes)
                assertEquals("42", inputSeconds)
            }
        }
    }
}
