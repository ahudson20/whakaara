package com.app.whakaara.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelectable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.ui.navigation.BottomNavigation
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class BottomNavigationTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectBottomNavigationItems(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                BottomNavigation(
                    navController = rememberNavController()
                )
            }
        }

        // Then
        onNodeWithText("Alarm").assertIsDisplayed().assertIsSelectable()
        onNodeWithText("Timer").assertIsDisplayed().assertIsSelectable()
        onNodeWithText("Stopwatch").assertIsDisplayed().assertIsSelectable()
    }
}
