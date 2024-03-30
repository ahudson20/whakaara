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
import java.util.Calendar

class CardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 13)
        }
        val alarm = Alarm(
            date = date,
            subTitle = "12:13 AM"
        )
        val timeToAlarm = getInitialTimeToAlarm(alarm.isEnabled, date)

        // When
        setContent {
            WhakaaraTheme {
                Card(
                    alarm = alarm,
                    is24HourFormat = true,
                    disable = {},
                    enable = {},
                    reset = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "12:13AM").assertIsDisplayed()
        onNodeWithText(text = timeToAlarm).assertIsDisplayed()
        onNodeWithTag(testTag = "alarm switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()
    }
}
