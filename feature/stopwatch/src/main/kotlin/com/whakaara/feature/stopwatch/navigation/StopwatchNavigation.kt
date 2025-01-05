package com.whakaara.feature.stopwatch.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.whakaara.core.LeafScreen
import com.whakaara.core.constants.GeneralConstants.DEEPLINK_STOPWATCH
import com.whakaara.feature.stopwatch.StopwatchRoute
import com.whakaara.feature.stopwatch.StopwatchViewModel

fun NavController.navigateToStopwatchScreen(navOptions: NavOptions? = null) {
    navigate(LeafScreen.Stopwatch.route, navOptions)
}

fun NavGraphBuilder.stopwatchScreen(
    viewModel: StopwatchViewModel
) {
    composable(
        route = LeafScreen.Stopwatch.route,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEPLINK_STOPWATCH
            }
        )
    ) {
        StopwatchRoute(
            viewModel = viewModel
        )
    }
}
