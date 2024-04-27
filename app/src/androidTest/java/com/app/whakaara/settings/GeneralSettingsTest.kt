package com.app.whakaara.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.settings.GeneralSettings
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class GeneralSettingsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given
        val preferencesState = PreferencesState()

        // When
        setContent {
            WhakaaraTheme {
                GeneralSettings(
                    preferencesState = preferencesState,
                    updatePreferences = {},
                    updateAllAlarmSubtitles = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "General settings").assertIsDisplayed()
        onNodeWithText(text = "Edit system time").assertIsDisplayed()
        onNodeWithText(text = "App settings").assertIsDisplayed()
        onNodeWithText(text = "Battery optimization").assertIsDisplayed()
        onNodeWithText(text = "Select ringtone").assertIsDisplayed()
        onNodeWithText(text = "App Theme").assertIsDisplayed()
        onNodeWithText(text = "System preference").assertIsDisplayed()
        onNodeWithText(text = "24 hour format").assertIsDisplayed()
        onNodeWithText(text = "If enabled, display using 24 hour format").assertIsDisplayed()
    }
}
