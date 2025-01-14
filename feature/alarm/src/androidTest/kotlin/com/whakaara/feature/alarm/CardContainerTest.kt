package com.whakaara.feature.alarm

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.alarm.ui.CardContainerSwipeToDismiss
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.preferences.TimeFormat
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class CardContainerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectDataAlarmsListEmpty(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                CardContainerSwipeToDismiss(
                    alarms = listOf(),
                    timeFormat = TimeFormat.TWELVE_HOURS,
                    delete = {},
                    disable = {},
                    enable = {},
                    reset = {},
                    getInitialTimeToAlarm = { _, _ -> "getInitialTimeToAlarm" }
                ) { "getTimeUntilAlarmFormatted" }
            }
        }

        // Then
        onNodeWithText(text = "No alarms to display").assertIsDisplayed()
    }

    @Test
    @Ignore("Flaky on pipeline")
    fun shouldDisplayCorrectDataAlarmsListNotEmpty(): Unit = with(composeTestRule) {
        // Given
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 13)
        }
        val alarms = listOf(
            Alarm(
                date = date,
                subTitle = "12:13 AM"
            ),
            Alarm(
                date = date,
                subTitle = "12:13 PM"
            )
        )

        // When
        setContent {
            WhakaaraTheme {
                CardContainerSwipeToDismiss(
                    alarms = alarms,
                    timeFormat = TimeFormat.TWELVE_HOURS,
                    delete = {},
                    disable = {},
                    enable = {},
                    reset = {},
                    getInitialTimeToAlarm = { _, _ -> "getInitialTimeToAlarm" }
                ) { "getTimeUntilAlarmFormatted" }
            }
        }

        // Then
        onNodeWithText(text = "12:13AM").assertIsDisplayed()
        onAllNodesWithTag(testTag = "alarm switch")[0]
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()

        onNodeWithText(text = "12:13PM").assertIsDisplayed()
        onAllNodesWithTag(testTag = "alarm switch")[1]
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()
    }
}
