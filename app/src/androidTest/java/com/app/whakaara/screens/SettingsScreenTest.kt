package com.app.whakaara.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.screens.SettingsScreen
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectDataDefaultState(): Unit = with(composeTestRule) {
        // Given
        val state = PreferencesState()

        // When
        setContent {
            WhakaaraTheme {
                SettingsScreen(
                    preferencesState = state,
                    updatePreferences = {},
                    updateAllAlarmSubtitles = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "Settings").assertIsDisplayed()

        onNodeWithText(text = "General settings").assertIsDisplayed()

        onNodeWithText(text = "Edit system time").assertIsDisplayed()

        onNodeWithText(text = "App settings").assertIsDisplayed()

        onNodeWithText(text = "Alarm Settings").assertIsDisplayed()

        onNodeWithText(text = "App Theme").performScrollTo().assertIsDisplayed()

        onNodeWithText(text = "Dynamic theming").performScrollTo().assertIsDisplayed()

        onNodeWithText(text = "24 hour format").performScrollTo().assertIsDisplayed()
        onNodeWithText(text = "If enabled, display using 24 hour format").assertIsDisplayed()

        onNodeWithText(text = "Alarm Settings").performScrollTo().assertIsDisplayed()

        onNodeWithText(text = "Next enabled").performScrollTo().assertIsDisplayed()
        onNodeWithText(text = "Sort alarms by next enabled").performScrollTo().assertIsDisplayed()

        onNodeWithText(text = "Vibrate when alarms go off").performScrollTo().assertIsDisplayed()
        onNodeWithTag(testTag = "alarm vibrate drop down")

        onNodeWithText(text = "Snooze").performScrollTo().assertIsDisplayed()
        onNodeWithText(text = "Allow alarms to be snoozed").assertIsDisplayed()

        onNodeWithText(text = "Snooze duration").performScrollTo().assertIsDisplayed()

        onNodeWithText(text = "Delete").performScrollTo().assertIsDisplayed()
        onNodeWithText(text = "Alarms are deleted after they go off").performScrollTo().assertIsDisplayed()

        onNodeWithText(text = "Auto silence").performScrollTo().assertIsDisplayed()
        onNodeWithText(text = "Set time after which alarms will be silenced").performScrollTo().assertIsDisplayed()

        onNodeWithText(text = "Create widget").performScrollTo().assertIsDisplayed()
        onNodeWithText(text = "Display next alarm in a widget").performScrollTo().assertIsDisplayed()

        onNodeWithText(text = "Timer settings").performScrollTo().assertIsDisplayed()
        onNodeWithTag(testTag = "timer vibrate switch").performScrollTo().assertIsDisplayed()
        onNodeWithTag(testTag = "timer vibrate dropdown").performScrollTo().assertIsDisplayed()
    }
}
