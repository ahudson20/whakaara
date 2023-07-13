package com.app.whakaara.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmDao
import com.app.whakaara.data.alarm.AlarmDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class AlarmDatabaseTest {
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
        alarmDatabase = Room
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
    fun insert_alarm_should_return_alarm_in_flow() = runTest {
        val alarm = Alarm(
            subTitle = "subtitle",
            date = Calendar.getInstance()
        )
        alarmDao.insert(alarm)

        alarmDao.getAllAlarmsFlow().test {
            val list = awaitItem()
            assert(list.contains(alarm))
            cancel()
        }
    }
}
