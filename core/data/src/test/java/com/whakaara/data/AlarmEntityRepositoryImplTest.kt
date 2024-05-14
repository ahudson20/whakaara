package com.whakaara.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.whakaara.data.alarm.AlarmRepository
import com.whakaara.data.alarm.AlarmRepositoryImpl
import com.whakaara.database.alarm.AlarmDao
import com.whakaara.database.alarm.entity.AlarmEntity
import com.whakaara.database.alarm.entity.asExternalModel
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
class AlarmEntityRepositoryImplTest {
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
        val alarmEntity = AlarmEntity(
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
        coEvery { alarmDao.getAllAlarmsFlow() } returns flowOf(listOf(alarmEntity))

        // Then
        repository.getAllAlarmsFlow().test {
            val list = awaitItem()
            assert(list.contains(alarmEntity.asExternalModel()))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `get all alarms`() = runTest {
        // Given
        val alarmEntity = AlarmEntity(
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
        coEvery { alarmDao.getAllAlarms() } returns listOf(alarmEntity)

        // When
        val alarmList = repository.getAllAlarms()

        // Then
        assert(alarmList.contains(alarmEntity.asExternalModel()))
    }

    @Test
    fun `get alarm by id`() = runTest {
        // Given
        val alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d")
        val alarmEntity = AlarmEntity(
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

        coEvery { alarmDao.getAlarmById(any()) } returns alarmEntity

        // When
        val response = repository.getAlarmById(alarmId)

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
        val alarmEntitySlot = slot<AlarmEntity>()
        val alarmEntity = AlarmEntity(
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
        repository.update(alarm = alarmEntity.asExternalModel())

        // Then
        coVerify(atLeast = 1) { alarmDao.updateAlarm(capture(alarmEntitySlot)) }
        with(alarmEntitySlot.captured) {
            assertEquals(UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"), alarmId)
            assertEquals("Alarm", title)
            assertEquals("subTitle", subTitle)
            assertEquals(true, isEnabled)
        }
    }

    @Test
    fun `insert alarm`() = runTest {
        // Given
        val alarmEntitySlot = slot<AlarmEntity>()
        val alarmEntity = AlarmEntity(
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
        repository.insert(alarm = alarmEntity.asExternalModel())

        // Then
        coVerify(atLeast = 1) { alarmDao.insert(capture(alarmEntitySlot)) }
        with(alarmEntitySlot.captured) {
            assertEquals(UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"), alarmId)
            assertEquals("Alarm", title)
            assertEquals("subTitle", subTitle)
            assertEquals(true, isEnabled)
        }
    }

    @Test
    fun `delete alarm`() = runTest {
        // Given
        val alarmEntitySlot = slot<AlarmEntity>()
        val alarmEntity = AlarmEntity(
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
        repository.delete(alarm = alarmEntity.asExternalModel())

        // Then
        coVerify(atLeast = 1) { alarmDao.deleteAlarm(capture(alarmEntitySlot)) }
        with(alarmEntitySlot.captured) {
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
