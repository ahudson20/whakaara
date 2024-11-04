package com.app.whakaara.bottomsheet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.state.BooleanStateEvent
import com.app.whakaara.state.ListStateEvent
import com.app.whakaara.state.StringStateEvent
import com.app.whakaara.state.UpdateBottomSheetDetailsAlarmInfo
import com.app.whakaara.ui.bottomsheet.details.BottomSheetDetailsAlarmInfo
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
                BottomSheetDetailsAlarmInfo(
                    updateBottomSheetDetailsAlarmInfo = UpdateBottomSheetDetailsAlarmInfo(
                        updateIsVibrationEnabled = BooleanStateEvent(
                            value = false
                        ),
                        updateIsSnoozeEnabled = BooleanStateEvent(
                            value = true
                        ),
                        updateDeleteAfterGoesOff = BooleanStateEvent(
                            value = false
                        ),
                        updateRepeatDaily = BooleanStateEvent(
                            value = false
                        ),
                        updateCheckedList = ListStateEvent(),
                        updateTitle = StringStateEvent(
                            value = "Alarm"
                        )
                    )
                )
            }
        }

        // Then
        onNodeWithText(text = "Vibrate").assertIsDisplayed()
        onNodeWithText(text = "Vibrate on alarm").assertIsDisplayed()
        onNodeWithTag(testTag = "vibrate switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOff()

        onNodeWithText(text = "Snooze").assertIsDisplayed()
        onNodeWithText(text = "Snooze on alarm").assertIsDisplayed()
        onNodeWithTag(testTag = "snooze switch")
            .assertIsDisplayed()
            .assertIsToggleable()
            .assertIsOn()

        onNodeWithText(text = "Single-use").assertIsDisplayed()
        onNodeWithText(text = "Alarm deletes after going off").assertIsDisplayed()
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
        onNodeWithText(text = "Alarm").assertIsDisplayed()
        onNodeWithText(text = "5 / 20").assertIsDisplayed()
    }
}
