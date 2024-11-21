package com.whakaara.feature.onboarding

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.onboarding.ui.DisableBatteryOptimizationOnboarding
import org.junit.Rule
import org.junit.Test

class DisableBatteryOptimizationOnboardingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                DisableBatteryOptimizationOnboarding()
            }
        }

        // Then
        onNodeWithText("Battery").assertIsDisplayed()
        onNodeWithText(
            "This app requires unrestricted battery settings, otherwise certain functionality may not work as expected!"
        ).assertIsDisplayed()
        onNodeWithText("Disable battery optimization").assertIsDisplayed().assertHasClickAction()
    }
}
