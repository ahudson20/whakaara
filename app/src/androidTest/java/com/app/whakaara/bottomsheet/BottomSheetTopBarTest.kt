package com.app.whakaara.bottomsheet

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.ui.bottomsheet.BottomSheetTopBar
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.chargemap.compose.numberpicker.FullHours
import com.dokar.sheets.BottomSheetState
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

class BottomSheetTopBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                BottomSheetTopBar(
                    coroutineScope = rememberCoroutineScope(),
                    sheetState = BottomSheetState(),
                    alarm = Alarm(
                        date = Calendar.getInstance(),
                        isEnabled = false,
                        subTitle = "10:03 AM"
                    ),
                    reset = {},
                    pickerValue = FullHours(
                        hours = 12,
                        minutes = 12
                    ),
                    isVibrationEnabled = true,
                    isSnoozeEnabled = true,
                    deleteAfterGoesOff = false,
                    bottomText = "bottomText",
                    title = "title",
                    is24HourFormat = true
                )
            }
        }

        // Then
        onNodeWithContentDescription(label = "Cancel alarm changes", ignoreCase = true).assertIsDisplayed()
        onNodeWithContentDescription(label = "Save alarm changes", ignoreCase = true).assertIsDisplayed()

        onNodeWithText(text = "title").assertIsDisplayed()
        onNodeWithText(text = "bottomText").assertIsDisplayed()
    }
}
