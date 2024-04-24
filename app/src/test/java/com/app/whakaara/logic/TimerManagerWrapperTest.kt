package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.app.NotificationCompat
import app.cash.turbine.test
import com.app.whakaara.data.datastore.PreferencesDataStore
import com.app.whakaara.utils.DateUtils
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.concurrent.TimeUnit

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
    private val managedCoroutineScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        app = mockk()
        alarmManager = mockk()
        notificationManager = mockk()
        timerNotificationBuilder = mockk()
        countDownTimerUtil = mockk()
        preferencesDatastore = mockk()

        timerManagerWrapper = TimerManagerWrapper(app, alarmManager, notificationManager, timerNotificationBuilder, countDownTimerUtil, preferencesDatastore, managedCoroutineScope)

        coEvery { countDownTimerUtil.countdown(any(), any(), any(), any()) } just Runs
        coEvery { countDownTimerUtil.cancel() } just Runs
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

    @Test
    fun `recreate active timer`() = runTest {
        // Given
        val millis = 123123L

        // When
        timerManagerWrapper.recreateActiveTimer(milliseconds = millis)

        // Then
        verify(exactly = 1) { countDownTimerUtil.cancel() }
        verify(exactly = 1) { countDownTimerUtil.countdown(any(), any(), any(), any()) }
        timerManagerWrapper.timerState.test {
            val state = awaitItem()
            with(state) {
                assertEquals(false, isTimerPaused)
                assertEquals(false, isStart)
                assertEquals(true, isTimerActive)
                assertEquals(millis, millisecondsFromTimerInput)
                assertEquals(TimeUnit.MILLISECONDS.toHours(millis).toString(), inputHours)
                assertEquals(TimeUnit.MILLISECONDS.toMinutes(millis).toString(), inputMinutes)
                assertEquals(TimeUnit.MILLISECONDS.toSeconds(millis).toString(), inputSeconds)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `recreate paused timer`() = runTest {
        // Given
        val millis = 123123L
        val millisFormatted = DateUtils.formatTimeForTimer(
            millis = millis
        )

        // When
        timerManagerWrapper.recreatePausedTimer(milliseconds = millis)

        // Then
        timerManagerWrapper.timerState.test {
            val state = awaitItem()
            with(state) {
                assertEquals(true, isTimerPaused)
                assertEquals(false, isStart)
                assertEquals(false, isTimerActive)
                assertEquals(millis, currentTime)
                assertEquals(millis, millisecondsFromTimerInput)
                assertEquals(millisFormatted, time)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `cancel notification`() = runTest {
        // Given
        val id = 300
        val notificationId = slot<Int>()
        coEvery { notificationManager.cancel(any()) } just Runs

        // When
        timerManagerWrapper.cancelNotification()

        // Then
        verify(exactly = 1) { notificationManager.cancel(capture(notificationId)) }
        assertEquals(id, notificationId.captured)
    }
}
