package com.app.whakaara.card

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.app.whakaara.ui.card.DismissBackground
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
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
