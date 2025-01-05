package com.whakaara.feature.alarm.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.whakaara.core.LeafScreen
import com.whakaara.core.constants.GeneralConstants.DEEPLINK_ALARM
import com.whakaara.feature.alarm.AlarmRoute
import com.whakaara.feature.alarm.AlarmViewModel

fun NavController.navigateToAlarmScreen(navOptions: NavOptions? = null) {
    navigate(LeafScreen.Alarm.route, navOptions)
}

fun NavGraphBuilder.alarmScreen(
    viewModel: AlarmViewModel
) {
    composable(
        route = LeafScreen.Alarm.route,
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEPLINK_ALARM
            }
        )
    ) {
        AlarmRoute(
            viewModel = viewModel
        )
    }
}
