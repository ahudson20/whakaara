package com.app.whakaara.screens

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.ui.screens.NotificationFullScreen
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class NotificationFullScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given
        val alarm = Alarm(
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
            },
            title = "First Alarm Title",
            subTitle = "First Alarm"
        )

        // When
        setContent {
            WhakaaraTheme {
                NotificationFullScreen(
                    alarm = alarm,
                    snooze = {},
                    disable = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "Snooze").assertIsDisplayed()
            .assertHasClickAction()

        onNodeWithText(text = "Dismiss").assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun shouldNotDisplaySnoozeButtonIfDisabled(): Unit = with(composeTestRule) {
        // Given
        val alarm = Alarm(
            date = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 12)
                set(Calendar.MINUTE, 34)
            },
            isSnoozeEnabled = false,
            title = "First Alarm Title",
            subTitle = "First Alarm"
        )

        // When
        setContent {
            WhakaaraTheme {
                NotificationFullScreen(
                    alarm = alarm,
                    snooze = {},
                    disable = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "Snooze").assertDoesNotExist()

        onNodeWithText(text = "Dismiss").assertIsDisplayed()
            .assertHasClickAction()
    }
}
