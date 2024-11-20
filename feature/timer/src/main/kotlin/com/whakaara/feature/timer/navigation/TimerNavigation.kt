package com.whakaara.feature.timer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.whakaara.core.LeafScreen
import com.whakaara.core.constants.GeneralConstants.DEEPLINK_TIMER
import com.whakaara.feature.timer.TimerRoute

fun NavController.navigateToTimerScreen(navOptions: NavOptions? = null) {
    navigate(LeafScreen.Timer.route, navOptions)
}

fun NavGraphBuilder.timerScreen() {
    composable(
        route = LeafScreen.Timer.route,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEPLINK_TIMER
            }
        )
    ) {
        TimerRoute()
    }
}
