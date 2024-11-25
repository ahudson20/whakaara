package com.whakaara.feature.alarm

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.preferences.TimeFormat
import com.whakaara.feature.alarm.ui.Card
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

        // When
        setContent {
            WhakaaraTheme {
                Card(
                    alarm = alarm,
                    timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
                    disable = {},
                    enable = {},
                    reset = {},
                    getInitialTimeToAlarm = { _, _ -> "getInitialTimeToAlarm" }
                ) { "getTimeUntilAlarmFormatted" }
            }
        }

        // Then
        onNodeWithText(text = "12:13AM").assertIsDisplayed()
        onNodeWithTag(testTag = "alarm switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()
    }
}
