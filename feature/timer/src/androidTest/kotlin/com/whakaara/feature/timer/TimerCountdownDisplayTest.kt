package com.whakaara.feature.timer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.timer.ui.TimerCountdownDisplay
import com.whakaara.model.preferences.TimeFormat
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

        // When
        setContent {
            WhakaaraTheme {
                TimerCountdownDisplay(
                    progress = progress,
                    time = time,
                    isPaused = false,
                    isStart = true,
                    millisecondsFromTimerInput = 0,
                    timeFormat = TimeFormat.TWELVE_HOURS
                )
            }
        }

        // Then
        onNodeWithText("00:10:00").assertIsDisplayed()
        onNodeWithText("No timer set!").assertIsDisplayed()
    }
}
