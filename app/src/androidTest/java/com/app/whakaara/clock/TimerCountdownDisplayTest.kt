package com.app.whakaara.clock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.ui.clock.TimerCountdownDisplay
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class TimerCountdownDisplayTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given
        val progress = 1.0F
        val time = "00:10:00"
        val finishTime = "2:34 PM"

        // When
        setContent {
            WhakaaraTheme {
                TimerCountdownDisplay(
                    progress = progress,
                    time = time,
                    finishTime = finishTime
                )
            }
        }

        // Then
        onNodeWithText("00:10:00").assertIsDisplayed()
        onNodeWithText("2:34 PM").assertIsDisplayed()
    }
}
