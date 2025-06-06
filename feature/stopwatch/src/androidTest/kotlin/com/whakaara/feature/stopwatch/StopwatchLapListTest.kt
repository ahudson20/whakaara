package com.whakaara.feature.stopwatch

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.stopwatch.ui.StopwatchLapList
import com.whakaara.model.stopwatch.Lap
import org.junit.Rule
import org.junit.Test

class StopwatchLapListTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given
        val lapList = mutableListOf(
            Lap(
                time = 1000L,
                diff = 250L
            ),
            Lap(
                time = 2000L,
                diff = 500L
            ),
            Lap(
                time = 3000L,
                diff = 750L
            )
        )

        // When
        setContent {
            WhakaaraTheme {
                StopwatchLapList(
                    lapList = lapList,
                    listState = rememberLazyListState()
                )
            }
        }

        // Then
        onNodeWithText("03").assertIsDisplayed()
        onNodeWithText("00:03:000").assertIsDisplayed()
        onNodeWithText("00:00:750")

        onNodeWithText("02").assertIsDisplayed()
        onNodeWithText("00:02:000").assertIsDisplayed()
        onNodeWithText("00:00:500")

        onNodeWithText("01").assertIsDisplayed()
        onNodeWithText("00:01:000").assertIsDisplayed()
        onNodeWithText("00:00:250")
    }
}
