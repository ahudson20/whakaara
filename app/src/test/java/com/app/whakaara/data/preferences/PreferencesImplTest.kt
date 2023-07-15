package com.app.whakaara.data.preferences

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
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

        // When
        coEvery { preferencesDao.getPreferencesFlow() } returns flowOf(preferences)

        // Then
        repository.getPreferencesFlow().test {
            assertEquals(preferences, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
