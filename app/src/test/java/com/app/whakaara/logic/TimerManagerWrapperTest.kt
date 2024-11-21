package com.app.whakaara.logic
//
//import android.app.AlarmManager
//import android.app.Application
//import android.app.NotificationManager
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.core.app.NotificationCompat
//import app.cash.turbine.test
//import com.app.whakaara.utility.DateUtils
//import com.whakaara.data.datastore.PreferencesDataStoreRepository
//import com.whakaara.test.MainDispatcherRule
//import io.mockk.Runs
//import io.mockk.coEvery
//import io.mockk.just
//import io.mockk.mockk
//import io.mockk.slot
//import io.mockk.verify
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.TestScope
//import kotlinx.coroutines.test.UnconfinedTestDispatcher
//import kotlinx.coroutines.test.runTest
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.rules.TestRule
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class TimerManagerWrapperTest {
//    @Rule
//    @JvmField
//    var rule: TestRule = InstantTaskExecutorRule()
//
//    @get:Rule
//    val mainDispatcherRule = MainDispatcherRule()
//
//    private lateinit var timerManagerWrapper: TimerManagerWrapper
//    private lateinit var app: Application
//    private lateinit var alarmManager: AlarmManager
//    private lateinit var notificationManager: NotificationManager
//    private lateinit var timerNotificationBuilder: NotificationCompat.Builder
//    private lateinit var countDownTimerUtil: CountDownTimerUtil
//    private lateinit var preferencesDatastore: PreferencesDataStoreRepository
//
//    private val testDispatcher = UnconfinedTestDispatcher()
//    private val managedCoroutineScope = TestScope(testDispatcher)
//
//    @Before
//    fun setUp() {
//        app = mockk()
//        alarmManager = mockk()
//        notificationManager = mockk()
//        timerNotificationBuilder = mockk()
//        countDownTimerUtil = mockk()
//        preferencesDatastore = mockk()
//
//        coEvery { countDownTimerUtil.countdown(any(), any(), any(), any()) } just Runs
//        coEvery { countDownTimerUtil.cancel() } just Runs
//
//        timerManagerWrapper = TimerManagerWrapper(app, alarmManager, notificationManager, timerNotificationBuilder, countDownTimerUtil, preferencesDatastore, managedCoroutineScope)
//    }
//
//    @Test
//    fun `update input hours should update state`() = runTest {
//        // Given
//        timerManagerWrapper.updateInputHours("42")
//
//        // When
//        timerManagerWrapper.timerState.test {
//            val state = awaitItem()
//            // Then
//            with(state) {
//                assertEquals("42", inputHours)
//                assertEquals("00", inputMinutes)
//                assertEquals("00", inputSeconds)
//            }
//        }
//    }
//
//    @Test
//    fun `update input minutes should update state`() = runTest {
//        // Given
//        timerManagerWrapper.updateInputMinutes("42")
//
//        // When
//        timerManagerWrapper.timerState.test {
//            val state = awaitItem()
//            // Then
//            with(state) {
//                assertEquals("00", inputHours)
//                assertEquals("42", inputMinutes)
//                assertEquals("00", inputSeconds)
//            }
//        }
//    }
//
//    @Test
//    fun `update input seconds should update state`() = runTest {
//        // Given
//        timerManagerWrapper.updateInputSeconds("42")
//
//        // When
//        timerManagerWrapper.timerState.test {
//            val state = awaitItem()
//            // Then
//            with(state) {
//                assertEquals("00", inputHours)
//                assertEquals("00", inputMinutes)
//                assertEquals("42", inputSeconds)
//            }
//        }
//    }
//
//    @Test
//    fun `recreate active timer`() = runTest {
//        // Given
//        val millis = 123123L
//        val inputHoursString = "01"
//        val inputMinutesString = "02"
//        val inputSecondsString = "03"
//
//        // When
//        timerManagerWrapper.recreateActiveTimer(
//            milliseconds = millis,
//            inputHours = inputHoursString,
//            inputMinutes = inputMinutesString,
//            inputSeconds = inputSecondsString
//        )
//
//        // Then
//        verify(exactly = 1) { countDownTimerUtil.cancel() }
//        verify(exactly = 1) { countDownTimerUtil.countdown(any(), any(), any(), any()) }
//        timerManagerWrapper.timerState.test {
//            val state = awaitItem()
//            with(state) {
//                assertEquals(false, isTimerPaused)
//                assertEquals(false, isStart)
//                assertEquals(true, isTimerActive)
//                assertEquals(millis, millisecondsFromTimerInput)
//                assertEquals("01", inputHours)
//                assertEquals("02", inputMinutes)
//                assertEquals("03", inputSeconds)
//                cancelAndIgnoreRemainingEvents()
//            }
//        }
//    }
//
//    @Test
//    fun `recreate paused timer`() = runTest {
//        // Given
//        val millis = 123123L
//        val millisFormatted = DateUtils.formatTimeForTimer(
//            millis = millis
//        )
//        val inputHours = "01"
//        val inputMinutes = "02"
//        val inputSeconds = "03"
//
//        // When
//        timerManagerWrapper.recreatePausedTimer(
//            milliseconds = millis,
//            inputHours = inputHours,
//            inputMinutes = inputMinutes,
//            inputSeconds = inputSeconds
//        )
//
//        // Then
//        timerManagerWrapper.timerState.test {
//            val state = awaitItem()
//            with(state) {
//                assertEquals(true, isTimerPaused)
//                assertEquals(false, isStart)
//                assertEquals(false, isTimerActive)
//                assertEquals(millis, currentTime)
//                assertEquals(millis, millisecondsFromTimerInput)
//                assertEquals(millisFormatted, time)
//                cancelAndIgnoreRemainingEvents()
//            }
//        }
//    }
//
//    @Test
//    fun `cancel notification`() = runTest {
//        // Given
//        val id = 300
//        val notificationId = slot<Int>()
//        coEvery { notificationManager.cancel(any()) } just Runs
//
//        // When
//        timerManagerWrapper.cancelNotification()
//
//        // Then
//        verify(exactly = 1) { notificationManager.cancel(capture(notificationId)) }
//        assertEquals(id, notificationId.captured)
//    }
//}
