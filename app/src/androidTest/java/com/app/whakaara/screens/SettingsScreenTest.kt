package com.app.whakaara.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.screens.SettingsScreen
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                SettingsScreen(
                    preferencesState = PreferencesState(),
                    updatePreferences = {},
                    updateAllAlarmSubtitles = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "General settings").assertIsDisplayed()
        onNodeWithText(text = "Edit system time").assertIsDisplayed()
        onNodeWithText(text = "App settings").assertIsDisplayed()

        onNodeWithText(text = "Alarm Settings").assertIsDisplayed()

        onNodeWithText(text = "Vibrate").assertIsDisplayed()
        onNodeWithText(text = "Vibrate when alarms go off").assertIsDisplayed()

        onNodeWithText(text = "Snooze").assertIsDisplayed()
        onNodeWithText(text = "Allow alarms to be snoozed").assertIsDisplayed()

        onNodeWithText(text = "Snooze duration").assertIsDisplayed()

        onNodeWithText(text = "Delete").assertIsDisplayed()
        onNodeWithText(text = "Alarms are deleted after they go off").assertIsDisplayed()

        onNodeWithText(text = "Auto silence").assertIsDisplayed()
        onNodeWithText(text = "Set time after which alarms will be silenced").assertIsDisplayed()
    }
}
