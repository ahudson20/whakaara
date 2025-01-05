package com.app.whakaara.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.whakaara.core.LeafScreen
import com.whakaara.core.RootScreen
import com.whakaara.feature.alarm.navigation.alarmScreen
import com.whakaara.feature.stopwatch.navigation.stopwatchScreen
import com.whakaara.feature.timer.navigation.timerScreen
import com.whakaara.onboarding.navigation.onboardingScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    shouldShowOnboarding: Boolean = false
) {
    NavHost(
        navController = navController,
        startDestination = if (shouldShowOnboarding) {
            RootScreen.Onboarding.route
        } else {
            RootScreen.Alarm.route
        }
    ) {
        addOnboardingRoute(navController)
        addAlarmRoute()
        addTimerRoute()
        addStopwatchRoute()
    }
}

private fun NavGraphBuilder.addAlarmRoute() {
    navigation(
        route = RootScreen.Alarm.route,
        startDestination = LeafScreen.Alarm.route
    ) {
        alarmScreen()
    }
}

private fun NavGraphBuilder.addTimerRoute() {
    navigation(
        route = RootScreen.Timer.route,
        startDestination = LeafScreen.Timer.route
    ) {
        timerScreen()
    }
}

private fun NavGraphBuilder.addStopwatchRoute() {
    navigation(
        route = RootScreen.Stopwatch.route,
        startDestination = LeafScreen.Stopwatch.route
    ) {
        stopwatchScreen()
    }
}

private fun NavGraphBuilder.addOnboardingRoute(navController: NavController) {
    navigation(
        route = RootScreen.Onboarding.route,
        startDestination = LeafScreen.Onboarding.route
    ) {
        onboardingScreen(
            navigateHome = {
                navController.navigate(RootScreen.Alarm.route) {
                    popUpTo(RootScreen.Onboarding.route) {
                        inclusive = true
                    }
                }
            }
        )
    }
}
