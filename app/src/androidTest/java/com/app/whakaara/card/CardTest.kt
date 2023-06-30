package com.app.whakaara.card

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.ui.card.Card
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.DateUtils.Companion.getInitialTimeToAlarm
import org.junit.Rule
import org.junit.Test

class CardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given
        val alarm = Alarm(
            hour = 12,
            minute = 13,
            subTitle = "12:13 AM"
        )
        val timeToAlarm = getInitialTimeToAlarm(alarm.isEnabled, alarm.hour, alarm.minute)

        // When
        setContent {
            WhakaaraTheme {
                Card(
                    alarm = alarm,
                    cancel = {},
                    enable = {},
                    reset = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "Alarm").assertIsDisplayed()
        onNodeWithText(text = "12:13 AM").assertIsDisplayed()
        onNodeWithText(text = timeToAlarm).assertIsDisplayed()
        onNodeWithTag(testTag = "alarm switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()
    }
}
