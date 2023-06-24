package com.app.whakaara

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.data.Alarm
import com.app.whakaara.ui.card.Card
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class CardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        setContent {
            WhakaaraTheme {
                Card(
                    alarm = Alarm(
                        hour = 12,
                        minute = 13,
                        subTitle = "12:13 AM"
                    ),
                    cancel = {},
                    enable = {},
                    reset = {}
                )
            }
        }

        onNodeWithText(text = "Alarm").assertIsDisplayed()
        onNodeWithText(text = "12:13 AM").assertIsDisplayed()
        onNodeWithText(text = "Alarm in 18 hours 41 minutes").assertDoesNotExist()
        onNodeWithTag(testTag = "alarm switch").assertIsDisplayed()
    }
}