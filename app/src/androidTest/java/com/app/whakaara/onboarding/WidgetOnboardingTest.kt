package com.app.whakaara.onboarding

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.app.whakaara.ui.onboarding.WidgetOnboarding
import com.app.whakaara.ui.theme.WhakaaraTheme
import org.junit.Rule
import org.junit.Test

class WidgetOnboardingTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shouldDisplayCorrectData(): Unit =
        with(composeTestRule) {
            // Given + When
            setContent {
                WhakaaraTheme {
                    WidgetOnboarding()
                }
            }

            // Then
            onNodeWithText("Widget").assertIsDisplayed()
            onNodeWithText("This app supports displaying your next scheduled alarm in a widget").assertIsDisplayed()
            onNodeWithText("Try adding it to the home screen and using it!").assertIsDisplayed()
            onNodeWithText("Create widget").assertIsDisplayed().assertHasClickAction()
        }
}
