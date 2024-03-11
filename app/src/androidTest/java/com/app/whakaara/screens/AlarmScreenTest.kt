package com.app.whakaara.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.state.AlarmState
import com.app.whakaara.ui.screens.AlarmScreen
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.DateUtils
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class AlarmScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given
        val firstAlarm = Alarm(
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
            },
            subTitle = "10:03PM"
        )

        val secondAlarm = Alarm(
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 7)
                set(Calendar.MINUTE, 34)
            },
            subTitle = "03:03AM"
        )
        val timeToFirstAlarm = DateUtils.getInitialTimeToAlarm(true, firstAlarm.date)
        val timeToSecondAlarm = DateUtils.getInitialTimeToAlarm(true, secondAlarm.date)

        // When
        setContent {
            WhakaaraTheme {
                AlarmScreen(
                    alarmState = AlarmState(listOf(firstAlarm, secondAlarm)),
                    is24HourFormat = true,
                    delete = {},
                    disable = {},
                    enable = {},
                    reset = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "10:03PM").assertIsDisplayed()
        onNodeWithText(text = timeToFirstAlarm).assertIsDisplayed()

        onNodeWithText(text = "03:03AM").assertIsDisplayed()
        onNodeWithText(text = timeToSecondAlarm).assertIsDisplayed()

        onAllNodesWithTag(testTag = "alarm switch", useUnmergedTree = true)
            .apply {
                fetchSemanticsNodes().forEachIndexed { i, _ ->
                    get(i).assertIsDisplayed().assertIsToggleable().assertIsOn()
                }
            }
    }
}
