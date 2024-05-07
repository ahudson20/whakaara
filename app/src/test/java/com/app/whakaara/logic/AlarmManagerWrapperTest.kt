package com.app.whakaara.logic

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.app.whakaara.R
import com.whakaara.test.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.Calendar

class AlarmManagerWrapperTest {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var alarmManagerWrapper: AlarmManagerWrapper
    private lateinit var app: Application
    private lateinit var alarmManager: AlarmManager
    private val context: Context = mockk<Context>(relaxed = true)

    @Before
    fun setUp() {
        app = mockk()
        alarmManager = mockk()

        every { app.applicationContext } returns context
        every { context.getString(R.string.card_alarm_sub_title_off) } returns "Off"

        alarmManagerWrapper = AlarmManagerWrapper(app, alarmManager)
    }

    @Test
    fun `get initial time to alarm - alarm disabled`() = runTest {
        // Given
        val isEnabled = false
        val time = Calendar.getInstance()

        // When
        val alarmTime = alarmManagerWrapper.getInitialTimeToAlarm(isEnabled = isEnabled, time = time)

        // Then
        assertEquals("Off", alarmTime)
    }
}
