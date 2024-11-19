package com.app.whakaara.bottomsheet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.ui.bottomsheet.details.BottomSheetDetailsTopBar
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class BottomSheetTopBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                BottomSheetDetailsTopBar(
                    bottomText = "bottomText",
                    title = "title"
                )
            }
        }

        // Then
        onNodeWithText(text = "title").assertIsDisplayed()
        onNodeWithText(text = "bottomText").assertIsDisplayed()
    }
}
