package com.app.whakaara.bottomsheet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.ui.bottomsheet.BottomSheetAlarmDetails
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class BottomSheetAlarmDetailsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                BottomSheetAlarmDetails(
                    isVibrationEnabled = false,
                    updateIsVibrationEnabled = {},
                    isSnoozeEnabled = true,
                    updateIsSnoozeEnabled = {},
                    deleteAfterGoesOff = false,
                    updateDeleteAfterGoesOff = {},
                    title = "Alarm",
                    updateTitle = {}
                )
            }
        }

        // Then
        onNodeWithText(text = "Vibrate when alarm sounds").assertIsDisplayed()
        onNodeWithTag(testTag = "vibrate switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOff()

        onNodeWithText(text = "Snooze").assertIsDisplayed()
        onNodeWithTag(testTag = "snooze switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()

        onNodeWithText(text = "Delete after goes off").assertIsDisplayed()
        onNodeWithTag(testTag = "delete switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOff()

        onNodeWithText(text = "Title").assertIsDisplayed()
        onNodeWithText(text = "Alarm").assertIsDisplayed()
    }
}
