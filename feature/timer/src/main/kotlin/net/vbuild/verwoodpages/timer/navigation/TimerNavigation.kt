package net.vbuild.verwoodpages.timer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.whakaara.core.LeafScreen
import net.vbuild.verwoodpages.timer.TimerRoute

fun NavController.navigateToTimerScreen(navOptions: NavOptions? = null) {
    navigate(LeafScreen.Timer.route, navOptions)
}

fun NavGraphBuilder.timerScreen() {
    composable(
        route = LeafScreen.Timer.route
    ) {
        TimerRoute()
    }
}
