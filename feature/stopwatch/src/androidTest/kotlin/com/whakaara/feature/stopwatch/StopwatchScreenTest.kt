package com.whakaara.feature.stopwatch

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.stopwatch.ui.StopwatchScreen
import com.whakaara.model.stopwatch.StopwatchState
import org.junit.Rule
import org.junit.Test

class StopwatchScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectDataDefaultState(): Unit = with(composeTestRule) {
        // Given
        val state = StopwatchState()

        // When
        setContent {
            WhakaaraTheme {
                StopwatchScreen(
                    stopwatchState = state,
                    onStart = {},
                    onPause = {},
                    onStop = {},
                    onLap = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "00:00").assertIsDisplayed()
        onNodeWithText(text = "000").assertIsDisplayed()

        onNodeWithTag("floating action button stop").assertIsNotDisplayed()
        onNodeWithTag("floating action button play-pause").assertIsDisplayed().assertHasClickAction()
    }

    @Test
    fun shouldDisplayStopButtonIfNotStart(): Unit = with(composeTestRule) {
        // Given
        val state = StopwatchState(
            isStart = false
        )

        // When
        setContent {
            WhakaaraTheme {
                StopwatchScreen(
                    stopwatchState = state,
                    onStart = {},
                    onPause = {},
                    onStop = {},
                    onLap = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "00:00").assertIsDisplayed()
        onNodeWithText(text = "000").assertIsDisplayed()

        onNodeWithTag("floating action button stop").assertIsDisplayed().assertHasClickAction()
        onNodeWithTag("floating action button play-pause").assertIsDisplayed().assertHasClickAction()
    }
}
