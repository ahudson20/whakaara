package com.whakaara.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.whakaara.data.datastore.PreferencesDataStoreRepository
import com.whakaara.data.datastore.PreferencesDataStoreRepositoryImpl
import com.whakaara.database.datastore.PreferencesDataStore
import com.whakaara.model.datastore.StopwatchDataStore
import com.whakaara.model.datastore.TimerStateDataStore
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class PreferencesDataStoreRepositoryImplTest {
    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var repository: PreferencesDataStoreRepository
    private lateinit var preferencesDataStore: PreferencesDataStore

    @Before
    fun setUp() {
        preferencesDataStore = mockk()
        repository = PreferencesDataStoreRepositoryImpl(preferencesDataStore = preferencesDataStore)
    }

    @Test
    fun `read background colour`() = runTest {
        // Given
        val colour = "colourString"

        // When
        coEvery { preferencesDataStore.readBackgroundColour } returns flowOf(colour)

        // Then
        repository.readBackgroundColour().test {
            val data = awaitItem()
            assertEquals(colour, data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `read text colour`() = runTest {
        // Given
        val textColour = "textColour"

        // When
        coEvery { preferencesDataStore.readTextColour } returns flowOf(textColour)

        // Then
        repository.readBackgroundColour().test {
            val data = awaitItem()
            assertEquals(textColour, data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `save colour`() = runTest {
        // Given
        val backgroundSlot = slot<String>()
        val textSlot = slot<String>()
        val background = "backgroundColour"
        val textColour = "textColour"

        coEvery { preferencesDataStore.saveColour(any(), any()) } just Runs

        // When
        repository.saveColour(background = background, text = textColour)

        // Then
        coVerify(exactly = 1) { preferencesDataStore.saveColour(capture(backgroundSlot), capture(textSlot)) }
        assertEquals("backgroundColour", backgroundSlot.captured)
        assertEquals("textColour", textSlot.captured)
    }

    @Test
    fun `read timer status`() = runTest {
        // Given
        val status = TimerStateDataStore(
            remainingTimeInMillis = 123L
        )

        // When
        coEvery { preferencesDataStore.readTimerStatus } returns flowOf(status)

        // Then
        repository.readTimerStatus().test {
            val data = awaitItem()
            with(data) {
                assertEquals(123L, remainingTimeInMillis)
                assertEquals(false, isActive)
                assertEquals(false, isPaused)
                assertEquals(0L, timeStamp)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `save timer data`() = runTest {
        // Given
        val stateSlot = slot<TimerStateDataStore>()
        val state = TimerStateDataStore(
            remainingTimeInMillis = 123L
        )
        coEvery { preferencesDataStore.saveTimerData(any()) } just Runs

        // When
        repository.saveTimerData(state = state)

        // Then
        coVerify(exactly = 1) { preferencesDataStore.saveTimerData(capture(stateSlot)) }
        with(stateSlot.captured) {
            assertEquals(123L, remainingTimeInMillis)
            assertEquals(false, isActive)
            assertEquals(false, isPaused)
            assertEquals(0L, timeStamp)
        }
    }

    @Test
    fun `read stopwatch state`() = runTest {
        // Given
        val state = StopwatchDataStore(
            timeMillis = 123L,
            lastTimeStamp = 456L,
            isActive = true
        )

        // When
        coEvery { preferencesDataStore.readStopwatchState } returns flowOf(state)

        // Then
        repository.readStopwatchState().test {
            val data = awaitItem()
            with(data) {
                assertEquals(123L, timeMillis)
                assertEquals(456L, lastTimeStamp)
                assertEquals("00:00:00:000", formattedTime)
                assertEquals(true, isActive)
                assertEquals(true, isStart)
                assertEquals(false, isPaused)
                assertEquals(0, lapList.size)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `save stopwatch state`() = runTest {
        // Given
        val stateSlot = slot<StopwatchDataStore>()
        val state = StopwatchDataStore(
            timeMillis = 123L,
            lastTimeStamp = 456L,
            isActive = true
        )
        coEvery { preferencesDataStore.saveStopwatchState(any()) } just Runs

        // When
        repository.saveStopwatchState(state = state)

        // Then
        coVerify(exactly = 1) { preferencesDataStore.saveStopwatchState(capture(stateSlot)) }
        with(stateSlot.captured) {
            assertEquals(123L, timeMillis)
            assertEquals(456L, lastTimeStamp)
            assertEquals("00:00:00:000", formattedTime)
            assertEquals(true, isActive)
            assertEquals(true, isStart)
            assertEquals(false, isPaused)
            assertEquals(0, lapList.size)
        }
    }

    @Test
    fun `clear stopwatch state`() = runTest {
        // Given
        coEvery { preferencesDataStore.clearStopwatchState() } just Runs

        // When
        repository.clearStopwatchState()

        // Then
        coVerify(exactly = 1) { preferencesDataStore.clearStopwatchState() }
    }
}
