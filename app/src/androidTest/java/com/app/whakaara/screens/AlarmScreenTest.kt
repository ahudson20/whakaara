package com.app.whakaara.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import com.whakaara.model.preferences.PreferencesState
import com.app.whakaara.state.events.AlarmEventCallbacks
import com.app.whakaara.ui.screens.AlarmScreen
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.alarm.Alarm
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
                    alarmEventCallbacks = object : AlarmEventCallbacks {
                        override fun create(alarm: Alarm) {}

                        override fun delete(alarm: Alarm) {}

                        override fun disable(alarm: Alarm) {}

                        override fun enable(alarm: Alarm) {}

                        override fun reset(alarm: Alarm) {}

                        override fun getInitialTimeToAlarm(
                            isEnabled: Boolean,
                            time: Calendar
                        ): String {
                            return ""
                        }

                        override fun getTimeUntilAlarmFormatted(date: Calendar): String {
                            return ""
                        }
                    }
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
