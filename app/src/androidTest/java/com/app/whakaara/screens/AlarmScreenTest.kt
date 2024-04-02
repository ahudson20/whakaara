package com.app.whakaara.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.screens.AlarmScreen
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class AlarmScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    @Ignore("Flaky")
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

        // When
        setContent {
            WhakaaraTheme {
                AlarmScreen(
                    alarms = listOf(firstAlarm, secondAlarm),
                    preferencesState = PreferencesState(),
                    delete = {},
                    disable = {},
                    enable = {},
                    reset = {},
                    create = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "10:03PM").assertIsDisplayed()

        onNodeWithText(text = "03:03AM").assertIsDisplayed()

        onAllNodesWithTag(testTag = "alarm switch", useUnmergedTree = true)
            .apply {
                fetchSemanticsNodes().forEachIndexed { i, _ ->
                    get(i).assertIsDisplayed().assertIsToggleable().assertIsOn()
                }
            }
    }
}
