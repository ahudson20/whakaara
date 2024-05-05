package com.app.whakaara.clock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.ui.clock.StopwatchDisplay
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class StopwatchDisplayTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayFullTimeWhenHoursNotEmpty(): Unit =
        with(composeTestRule) {
            // Given
            val time = "09:10:11:123"

            // When
            setContent {
                WhakaaraTheme {
                    StopwatchDisplay(
                        formattedTime = time,
                    )
                }
            }

            // Then
            onNodeWithText("09:10:11").assertIsDisplayed()
            onNodeWithText("123")
        }

    @Test
    fun shouldNotDisplayFullTimeWhenHoursEmpty(): Unit =
        with(composeTestRule) {
            // Given
            val time = "00:10:11:123"

            // When
            setContent {
                WhakaaraTheme {
                    StopwatchDisplay(
                        formattedTime = time,
                    )
                }
            }

            // Then
            onNodeWithText("10:11").assertIsDisplayed()
            onNodeWithText("123")
        }
}
