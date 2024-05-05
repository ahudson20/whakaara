package com.whakaara.database.database

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.whakaara.database.preferences.PreferencesDao
import com.whakaara.database.preferences.PreferencesDatabase
import com.whakaara.database.preferences.entity.PreferencesEntity
import com.whakaara.model.preferences.SettingsTime
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class PreferencesEntityDatabaseTest {
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
        preferencesDatabase =
            Room
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
    fun insert_should_return_preferences_in_flow() =
        runTest {
            val preferencesEntity =
                PreferencesEntity(
                    id = 100,
                    isVibrateEnabled = false,
                    isSnoozeEnabled = true,
                    deleteAfterGoesOff = false,
                    autoSilenceTime = SettingsTime.TEN,
                    snoozeTime = SettingsTime.TEN,
                )
            preferencesDao.insert(preferencesEntity = preferencesEntity)

            preferencesDao.getPreferencesFlow().test {
                assertEquals(preferencesEntity, awaitItem())
                cancel()
            }
        }

    @Test
    fun update_preference_should_return_updated() =
        runTest {
            // Given
            val preferencesEntity =
                PreferencesEntity(
                    id = 100,
                    isVibrateEnabled = false,
                    isSnoozeEnabled = true,
                    deleteAfterGoesOff = false,
                    autoSilenceTime = SettingsTime.TEN,
                    snoozeTime = SettingsTime.TEN,
                )
            preferencesDao.insert(preferencesEntity = preferencesEntity)

            // When
            preferencesDao.updatePreferences(
                preferencesEntity =
                    preferencesEntity.copy(
                        id = 100,
                        autoSilenceTime = SettingsTime.FIFTEEN,
                        snoozeTime = SettingsTime.FIFTEEN,
                    ),
            )

            preferencesDao.getPreferencesFlow().test {
                val updated = awaitItem()
                updated.apply {
                    assertNotEquals(SettingsTime.TEN, this.autoSilenceTime)
                    assertNotEquals(SettingsTime.TEN, this.snoozeTime)

                    assertEquals(SettingsTime.FIFTEEN, this.autoSilenceTime)
                    assertEquals(SettingsTime.FIFTEEN, this.snoozeTime)
                }
                cancel()
            }
        }
}
