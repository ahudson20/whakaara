package com.app.whakaara.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.PreferencesDao
import com.app.whakaara.data.preferences.PreferencesDatabase
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
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class PreferencesDatabaseTest {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var preferencesDao: PreferencesDao
    private lateinit var preferencesDatabase: PreferencesDatabase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        preferencesDatabase = Room
            .inMemoryDatabaseBuilder(context, PreferencesDatabase::class.java)
            .build()
        preferencesDao = preferencesDatabase.preferencesDao()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        preferencesDatabase.close()
    }

    @Test
    fun insert_should_return_preferences_in_flow() = runTest {
        val preferences = Preferences(
            id = 100,
            isVibrateEnabled = false,
            isSnoozeEnabled = true,
            deleteAfterGoesOff = false,
            autoSilenceTime = 123,
            snoozeTime = 321
        )
        preferencesDao.insert(preferences = preferences)

        preferencesDao.getPreferencesFlow().test {
            assertEquals(preferences, awaitItem())
            cancel()
        }
    }
}
