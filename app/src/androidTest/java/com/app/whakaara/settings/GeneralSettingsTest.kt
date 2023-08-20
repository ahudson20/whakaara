package com.app.whakaara.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.ui.settings.GeneralSettings
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class GeneralSettingsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                GeneralSettings()
            }
        }

        // Then
        onNodeWithText(text = "General settings").assertIsDisplayed()
        onNodeWithText(text = "Edit system time").assertIsDisplayed()
        onNodeWithText(text = "App settings").assertIsDisplayed()
    }
}
