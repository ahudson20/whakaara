package com.whakaara.database.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.whakaara.database.alarm.AlarmDao
import com.whakaara.database.alarm.AlarmDatabase
import com.whakaara.database.alarm.entity.AlarmEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.util.Calendar
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class AlarmEntityDatabaseTest {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var alarmDao: AlarmDao
    private lateinit var alarmDatabase: AlarmDatabase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        alarmDatabase =
            Room
                .inMemoryDatabaseBuilder(context, AlarmDatabase::class.java)
                .build()
        alarmDao = alarmDatabase.alarmDao()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        alarmDatabase.close()
    }

    @Test
    fun insert_alarm_should_return_alarm_in_flow() =
        runTest {
            // Given
            val alarmEntity =
                AlarmEntity(
                    subTitle = "subTitle",
                    date = Calendar.getInstance(),
                )

            // When
            alarmDao.insert(alarmEntity)

            // Then
            alarmDao.getAllAlarmsFlow().test {
                val list = awaitItem()
                assert(list.contains(alarmEntity))
                cancel()
            }
        }

    @Test
    fun insert_alarm_should_return_alarm_not_in_flow() =
        runTest {
            // Given
            val alarmEntity =
                AlarmEntity(
                    title = "title",
                    subTitle = "subTitle",
                    date = Calendar.getInstance(),
                )

            // When
            alarmDao.insert(alarmEntity)

            // Then
            val alarms = alarmDao.getAllAlarms()
            assert(alarms.contains(alarmEntity))
        }

    @Test
    fun update_alarm_should_return_alarm_get_by_id() =
        runTest {
            // Given
            val alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d")
            val alarmEntity =
                AlarmEntity(
                    alarmId = alarmId,
                    title = "title",
                    subTitle = "subTitle",
                    date = Calendar.getInstance(),
                )
            alarmDao.insert(alarmEntity)

            // When
            alarmDao.updateAlarm(
                alarmEntity =
                    alarmEntity.copy(
                        title = "updatedTitle",
                        subTitle = "updatedSubTitle",
                    ),
            )

            // Then
            alarmDao.getAlarmById(id = alarmId).apply {
                assertNotEquals("title", this.title)
                assertNotEquals("subTitle", this.subTitle)

                assertEquals("updatedTitle", this.title)
                assertEquals("updatedSubTitle", this.subTitle)
            }
        }

    @Test
    fun delete_alarm_should_not_return_alarm() =
        runTest {
            // Given
            val alarmEntity =
                AlarmEntity(
                    alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d"),
                    title = "title",
                    subTitle = "subTitle",
                    date = Calendar.getInstance(),
                )
            alarmDao.insert(alarmEntity)

            // When
            alarmDao.deleteAlarm(alarmEntity)

            // Then
            alarmDao.getAllAlarmsFlow().test {
                val list = awaitItem()
                assert(!list.contains(alarmEntity))
                cancel()
            }
        }

    @Test
    fun delete_alarm_by_id_should_not_return_alarm() =
        runTest {
            // Given
            val alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d")
            val alarmEntity =
                AlarmEntity(
                    alarmId = alarmId,
                    title = "title",
                    subTitle = "subTitle",
                    date = Calendar.getInstance(),
                )
            alarmDao.insert(alarmEntity)

            // When
            alarmDao.deleteAlarmById(id = alarmId)

            // Then
            alarmDao.getAllAlarmsFlow().test {
                val list = awaitItem()
                assert(!list.contains(alarmEntity))
                cancel()
            }
        }

    @Test
    fun alarm_is_enabled() =
        runTest {
            // Given
            val alarmId = UUID.fromString("19de4fcc-1c68-485c-b817-0290faec649d")
            val alarmEntity =
                AlarmEntity(
                    alarmId = alarmId,
                    title = "title",
                    subTitle = "subTitle",
                    date = Calendar.getInstance(),
                    isEnabled = true,
                )
            alarmDao.insert(alarmEntity)

            // When
            alarmDao.isEnabled(id = alarmId, isEnabled = false)

            // Then
            alarmDao.getAlarmById(id = alarmId).apply {
                assertEquals(false, this.isEnabled)
            }
        }
}
