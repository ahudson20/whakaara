package com.whakaara.feature.alarm

import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.alarm.ui.DismissBackground
import org.junit.Rule
import org.junit.Test

class SwipeToDismissTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayDeleteIcon(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                DismissBackground(
                    dismissState = rememberSwipeToDismissBoxState()
                )
            }
        }

        // Then
        onNodeWithContentDescription(label = "delete icon", ignoreCase = true).assertIsDisplayed()
    }
}
