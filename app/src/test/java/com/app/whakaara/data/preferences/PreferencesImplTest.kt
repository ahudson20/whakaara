package com.app.whakaara.data.preferences

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PreferencesImplTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var repository: PreferencesRepository
    private lateinit var preferencesDao: PreferencesDao

    @Before
    fun setUp() {
        preferencesDao = mockk()
        repository = PreferencesImpl(preferencesDao = preferencesDao)
    }

    @Test
    fun `get preferences flow`() = runTest {
        // Given
        val preferences = Preferences()
        coEvery { preferencesDao.getPreferencesFlow() } returns flowOf(preferences)

        // When
        repository.getPreferencesFlow().test {
            // Then
            assertEquals(preferences, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insert preferences`() = runTest {
        // Given
        val preferences = Preferences(
            autoSilenceTime = SettingsTime.TEN,
            snoozeTime = SettingsTime.TEN
        )
        val preferencesSlot = slot<Preferences>()
        coEvery { preferencesDao.insert(any()) } returns mockk()

        // When
        repository.insert(preferences = preferences)

        // Then
        coVerify(atLeast = 1) { preferencesDao.insert(capture(preferencesSlot)) }
        with(preferencesSlot.captured) {
            assertEquals(10, autoSilenceTime.value)
            assertEquals(10, snoozeTime.value)
        }
    }

    @Test
    fun `update preferences`() = runTest {
        // Given
        val preferences = Preferences(
            autoSilenceTime = SettingsTime.FIFTEEN,
            snoozeTime = SettingsTime.TEN
        )
        val preferencesSlot = slot<Preferences>()
        coEvery { preferencesDao.updatePreferences(any()) } returns mockk()

        // When
        repository.updatePreferences(preferences = preferences)

        // Then
        coVerify(atLeast = 1) { preferencesDao.updatePreferences(capture(preferencesSlot)) }
        with(preferencesSlot.captured) {
            assertEquals(15, autoSilenceTime.value)
            assertEquals(10, snoozeTime.value)
        }
    }
}
