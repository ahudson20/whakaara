package com.app.whakaara.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.ui.onboarding.WelcomeOnboarding
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class WelcomeOnboardingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                WelcomeOnboarding()
            }
        }

        // Then
        onNodeWithText("Widget").assertIsDisplayed()
        onNodeWithText("This screen will guide you through the most important setup steps").assertIsDisplayed()
        onNodeWithContentDescription("whakaara app icon").assertIsDisplayed()
    }
}
