package com.app.whakaara.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.ui.settings.AlarmSettings
import org.junit.Rule
import org.junit.Test

class AlarmSettingsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            AlarmSettings(
                preferencesState = PreferencesState(),
                updatePreferences = {}
            )
        }

        // Then
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
