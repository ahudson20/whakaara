package net.vbuild.verwoodpages.alarm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.whakaara.core.LeafScreen
import com.whakaara.core.constants.GeneralConstants.DEEPLINK_ALARM
import net.vbuild.verwoodpages.alarm.AlarmRoute

fun NavController.navigateToAlarmScreen(navOptions: NavOptions? = null) {
    navigate(LeafScreen.Alarm.route, navOptions)
}

fun NavGraphBuilder.alarmScreen() {
    composable(
        route = LeafScreen.Alarm.route,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEPLINK_ALARM
            }
        )
    ) {
        AlarmRoute()
    }
}
