package com.whakaara.onboarding

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.onboarding.ui.NotificationsOnboarding
import org.junit.Rule
import org.junit.Test

class NotificationsOnboardingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit = with(composeTestRule) {
        // Given + When
        setContent {
            WhakaaraTheme {
                NotificationsOnboarding(
                    snackbarHostState = SnackbarHostState()
                )
            }
        }

        // Then
        onNodeWithText("Notifications").assertIsDisplayed()
        onNodeWithText("This app requires permission to send you notifications!").assertIsDisplayed()
        onNodeWithText("Enable notifications").assertIsDisplayed().assertHasClickAction()
    }
}
