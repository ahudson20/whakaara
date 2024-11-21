package com.whakaara.feature.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.onboarding.ui.WelcomeOnboarding
import org.junit.Rule
import org.junit.Test

class WelcomeOnboardingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit =
        with(composeTestRule) {
            // Given + When
            setContent {
                WhakaaraTheme {
                    WelcomeOnboarding()
                }
            }

            // Then
            onNodeWithContentDescription("whakaara app icon").assertIsDisplayed()
            onNodeWithText("Welcome to whakaara").assertIsDisplayed()
            onNodeWithText("This screen will guide you through the most important setup steps").assertIsDisplayed()
        }
}
