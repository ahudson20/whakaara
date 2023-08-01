package com.app.whakaara.clock

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.ui.clock.Timer
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class TimerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                Timer(
                    formattedTime = "01:01:01"
                )
            }
        }

        // Then
        onNodeWithText("01:01:01").assertIsDisplayed()

        onNodeWithContentDescription("Start timer icon button").assertIsDisplayed().assertHasClickAction()
        onNodeWithContentDescription("Pause timer icon button").assertIsDisplayed().assertHasClickAction()
        onNodeWithContentDescription("Stop timer icon button").assertIsDisplayed().assertHasClickAction()
    }
}
