package com.whakaara.onboarding.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.whakaara.core.LeafScreen
import com.whakaara.onboarding.OnboardingRoute

fun NavController.navigateToOnboarding(navOptions: NavOptions? = null) {
    navigate(LeafScreen.Onboarding.route, navOptions)
}

fun NavGraphBuilder.onboardingScreen(
    navigateHome: () -> Unit
) {
    composable(
        route = LeafScreen.Onboarding.route
    ) {
        OnboardingRoute(
            navigateHome = navigateHome
        )
    }
}
