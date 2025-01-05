package com.whakaara.feature.stopwatch

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.stopwatch.ui.Stopwatch
import com.whakaara.model.stopwatch.StopwatchState
import org.junit.Rule
import org.junit.Test

class StopwatchTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayPlayButtonOnStart(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                Stopwatch(
                    stopwatchState = StopwatchState(
                        formattedTime = "01:01:01:001",
                        isActive = false,
                        isStart = true
                    )
                )
            }
        }

        // Then
        onNodeWithText("01:01:01").assertIsDisplayed()
        onNodeWithText("001").assertIsDisplayed()

        onNodeWithContentDescription("Start timer icon button").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun shouldDisplayPlayStopButtonOnActive(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                Stopwatch(
                    stopwatchState = StopwatchState(
                        formattedTime = "01:01:01:001",
                        isActive = false,
                        isStart = false
                    )
                )
            }
        }

        // Then
        onNodeWithText("01:01:01").assertIsDisplayed()
        onNodeWithText("001").assertIsDisplayed()

        onNodeWithContentDescription("Start timer icon button").assertIsDisplayed().assertHasClickAction()
        onNodeWithContentDescription("Stop timer icon button").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun shouldDisplayPauseStopButtonOnActive(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                Stopwatch(
                    stopwatchState = StopwatchState(
                        formattedTime = "01:01:01:001",
                        isActive = true,
                        isStart = false
                    )
                )
            }
        }

        // Then
        onNodeWithText("01:01:01").assertIsDisplayed()
        onNodeWithText("001").assertIsDisplayed()

        onNodeWithContentDescription(label = "Pause timer icon button").assertIsDisplayed().assertHasClickAction()
        onNodeWithContentDescription(label = "Stop timer icon button").assertIsDisplayed().assertHasClickAction()
        onNodeWithContentDescription(label = "Extra fab icon button").assertIsDisplayed().assertHasClickAction()
    }
}
