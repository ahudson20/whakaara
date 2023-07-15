package com.app.whakaara.data.alarm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Calendar
import java.util.UUID

@RunWith(JUnit4::class)
class AlarmRepositoryImplTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var repository: AlarmRepository
    private lateinit var alarmDao: AlarmDao

    @Before
    fun setUp() {
        alarmDao = mockk()
        repository = AlarmRepositoryImpl(alarmDao = alarmDao)
    }

    @Test
    fun `get all alarms`() = runTest {
        // Given
        val alarm = Alarm(
            alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"),
            subTitle = "subTitle",
            date = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2023)
                set(Calendar.DAY_OF_MONTH, 13)
                set(Calendar.MONTH, 6)
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
                set(Calendar.SECOND, 0)
            }
        )

        // When
        coEvery { alarmDao.getAllAlarmsFlow() } returns flowOf(listOf(alarm))

        // Then
        repository.getAllAlarmsFlow().test {
            val list = awaitItem()
            assert(list.contains(alarm))
            cancelAndIgnoreRemainingEvents()
        }
    }
}
