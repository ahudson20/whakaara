package com.app.whakaara.bottomsheet

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.ui.bottomsheet.details.BottomSheetDetailsContent
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.dokar.sheets.rememberBottomSheetState
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class BottomSheetDetailsContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given
        val date = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21)
            set(Calendar.MINUTE, 56)
        }

        val alarm = Alarm(
            date = date,
            subTitle = "12:13 PM"
        )

        // When
        setContent {
            WhakaaraTheme {
                BottomSheetDetailsContent(
                    alarm = alarm,
                    timeToAlarm = "Alarm in 1 hour 12 minutes",
                    is24HourFormat = false,
                    sheetState = rememberBottomSheetState(),
                    reset = {}
                )
            }
        }

        // Then
        onAllNodesWithText(text = "Alarm")[0].assertIsDisplayed()
        onNodeWithText(text = "Alarm in 1 hour 12 minutes").assertIsDisplayed()

        onNodeWithText("21").assertIsDisplayed()
        onNodeWithText("56").assertIsDisplayed()

        onNodeWithText(text = "Vibrate").assertIsDisplayed()
        onNodeWithText(text = "Vibrate on alarm").assertIsDisplayed()
        onNodeWithTag(testTag = "vibrate switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()

        onNodeWithText(text = "Snooze").assertIsDisplayed()
        onNodeWithText(text = "Snooze on alarm").assertIsDisplayed()
        onNodeWithTag(testTag = "snooze switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()

        onNodeWithText(text = "Single-use").assertIsDisplayed()
        onNodeWithText(text = "Alarm will delete after going off").assertIsDisplayed()
        onNodeWithTag(testTag = "delete switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOff()

        onNodeWithText("Repeating").assertIsDisplayed()
        onNodeWithText("Alarm will repeat daily").assertIsDisplayed()

        onNodeWithText("Custom").assertIsDisplayed()
        onNodeWithText("Select certain days to repeat alarm").assertIsDisplayed()

        onAllNodesWithTag(testTag = "segmentedButton", useUnmergedTree = true)
            .apply {
                fetchSemanticsNodes().forEachIndexed { i, _ ->
                    get(i).assertIsDisplayed().assertIsToggleable().assertIsOff()
                }
            }

        onNodeWithText(text = "Title").assertIsDisplayed()
        onAllNodesWithText(text = "Alarm")[1].assertIsDisplayed().assertIsNotFocused()
        onNodeWithText(text = "5 / 20").assertIsDisplayed()

        onNodeWithText("Cancel").assertIsDisplayed().assertHasClickAction()
        onNodeWithText("Save").assertIsDisplayed().assertHasClickAction()
    }
}
