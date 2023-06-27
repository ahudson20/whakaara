package com.app.whakaara.card

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDismissState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.app.whakaara.ui.card.DismissBackground
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class SwipeToDismissTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalMaterialApi::class)
    @Test
    fun shouldDisplayDeleteIcon(): Unit = with(composeTestRule) {
        setContent {
            WhakaaraTheme {
                DismissBackground(
                    dismissState = rememberDismissState()
                )
            }
        }
        onNodeWithContentDescription(label = "delete icon", ignoreCase = true).assertIsDisplayed()
    }
}
