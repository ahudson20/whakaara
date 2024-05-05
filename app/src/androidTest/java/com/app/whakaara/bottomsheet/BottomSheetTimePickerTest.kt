package com.app.whakaara.bottomsheet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.state.HoursUpdateEvent
import com.app.whakaara.ui.bottomsheet.details.BottomSheetTimePicker
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.chargemap.compose.numberpicker.FullHours
import org.junit.Rule
import org.junit.Test

class BottomSheetTimePickerTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit =
        with(composeTestRule) {
            // Given + When
            setContent {
                WhakaaraTheme {
                    BottomSheetTimePicker(
                        updatePickerValue =
                            HoursUpdateEvent(
                                value =
                                    FullHours(
                                        hours = 12,
                                        minutes = 30,
                                    ),
                            ),
                    )
                }
            }

            // Then
            onNodeWithText("12").assertIsDisplayed()
            onNodeWithText("30").assertIsDisplayed()
        }
}
