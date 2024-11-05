package net.vbuild.verwoodpages.stopwatch.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.whakaara.core.LeafScreen
import net.vbuild.verwoodpages.stopwatch.StopwatchRoute

fun NavController.navigateToStopwatchScreen(navOptions: NavOptions? = null) {
    navigate(LeafScreen.Stopwatch.route, navOptions)
}

fun NavGraphBuilder.stopwatchScreen() {
    composable(
        route = LeafScreen.Stopwatch.route
    ) {
        StopwatchRoute()
    }
}
