package com.app.whakaara.data.alarm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
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
    fun `get all alarms flow`() = runTest {
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
        coEvery { alarmDao.getAllAlarms() } returns listOf(alarm)

        // When
        val alarmList = repository.getAllAlarms()

        // Then
        assert(alarmList.contains(alarm))
    }

    @Test
    fun `get alarm by id`() = runTest {
        // Given
        val alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d")
        val alarm = Alarm(
            alarmId = alarmId,
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
        val alarmIdSlot = slot<UUID>()

        coEvery { alarmDao.getAlarmById(any()) } returns alarm

        // When
        val response =
            repository.getAlarmById(alarmId)

        // Then
        coVerify(atLeast = 1) { alarmDao.getAlarmById(capture(alarmIdSlot)) }
        assertEquals(alarmId, alarmIdSlot.captured)
        with(response) {
            assertEquals(alarmId, alarmId)
            assertEquals("Alarm", title)
            assertEquals("subTitle", subTitle)
        }
    }

    @Test
    fun `update alarm`() = runTest {
        // Given
        val alarmSlot = slot<Alarm>()
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
        coEvery { alarmDao.updateAlarm(any()) } returns mockk()

        // When
        repository.update(alarm = alarm)

        // Then
        coVerify(atLeast = 1) { alarmDao.updateAlarm(capture(alarmSlot)) }
        with(alarmSlot.captured) {
            assertEquals(UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"), alarmId)
            assertEquals("Alarm", title)
            assertEquals("subTitle", subTitle)
            assertEquals(true, isEnabled)
        }
    }

    @Test
    fun `insert alarm`() = runTest {
        // Given
        val alarmSlot = slot<Alarm>()
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

        coEvery { alarmDao.insert(any()) } returns mockk()

        // When
        repository.insert(alarm = alarm)

        // Then
        coVerify(atLeast = 1) { alarmDao.insert(capture(alarmSlot)) }
        with(alarmSlot.captured) {
            assertEquals(UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"), alarmId)
            assertEquals("Alarm", title)
            assertEquals("subTitle", subTitle)
            assertEquals(true, isEnabled)
        }
    }

    @Test
    fun `delete alarm`() = runTest {
        // Given
        val alarmSlot = slot<Alarm>()
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

        coEvery { alarmDao.deleteAlarm(any()) } returns mockk()

        // When
        repository.delete(alarm = alarm)

        // Then
        coVerify(atLeast = 1) { alarmDao.deleteAlarm(capture(alarmSlot)) }
        with(alarmSlot.captured) {
            assertEquals(UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"), alarmId)
            assertEquals("Alarm", title)
            assertEquals("subTitle", subTitle)
            assertEquals(true, isEnabled)
        }
    }

    @Test
    fun `delete alarm by id`() = runTest {
        // Given
        val alarmIdSlot = slot<UUID>()
        val alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d")

        coEvery { alarmDao.deleteAlarmById(any()) } returns mockk()

        // When
        repository.deleteAlarmById(id = alarmId)

        // Then
        coVerify(atLeast = 1) { alarmDao.deleteAlarmById(capture(alarmIdSlot)) }
        assertEquals(
            UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"),
            alarmIdSlot.captured
        )
    }

    @Test
    fun `alarm is enabled`() = runTest {
        // Given
        val isEnabledSlot = slot<Boolean>()
        val alarmIdSlot = slot<UUID>()
        val alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d")

        coEvery { alarmDao.isEnabled(any(), any()) } returns mockk()

        // When
        repository.isEnabled(id = alarmId, isEnabled = true)

        // Then
        coVerify(atLeast = 1) { alarmDao.isEnabled(capture(alarmIdSlot), capture(isEnabledSlot)) }
        assertEquals(
            UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"),
            alarmIdSlot.captured
        )
        assertEquals(
            true,
            isEnabledSlot.captured
        )
    }
}
