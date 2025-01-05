package com.app.whakaara.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.ui.settings.TimerSettings
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.preferences.PreferencesState
import org.junit.Rule
import org.junit.Test

class TimerSettingsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit =
        with(composeTestRule) {
            // Given
            val state = PreferencesState()

            // When
            setContent {
                WhakaaraTheme {
                    TimerSettings(
                        preferencesState = state,
                        updatePreferences = {}
                    )
                }
            }

            // Then
            onNodeWithText(text = "Timer settings").assertIsDisplayed()

            onNodeWithText(text = "Select timer sound")

            onNodeWithText(text = "Vibrate").assertIsDisplayed()
            onNodeWithText(text = "Vibrate when timer goes off").assertIsDisplayed()

            onNodeWithText(text = "Vibration pattern").assertIsDisplayed()

            onNodeWithText(text = "Auto-restart").assertIsDisplayed()
            onNodeWithText(text = "Timer automatically restarts on click").assertIsDisplayed()
        }
}
