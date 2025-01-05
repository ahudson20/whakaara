package com.app.whakaara.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.whakaara.core.LeafScreen
import com.whakaara.core.RootScreen
import com.whakaara.feature.alarm.AlarmViewModel
import com.whakaara.feature.alarm.navigation.alarmScreen
import com.whakaara.feature.stopwatch.StopwatchViewModel
import com.whakaara.feature.stopwatch.navigation.stopwatchScreen
import com.whakaara.feature.timer.TimerViewModel
import com.whakaara.feature.timer.navigation.timerScreen
import com.whakaara.onboarding.navigation.onboardingScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    stopwatchViewModel: StopwatchViewModel,
    timerViewModel: TimerViewModel,
    alarmViewModel: AlarmViewModel,
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
        addAlarmRoute(
            viewModel = alarmViewModel
        )
        addTimerRoute(
            viewModel = timerViewModel
        )
        addStopwatchRoute(
            viewModel = stopwatchViewModel
        )
    }
}

private fun NavGraphBuilder.addAlarmRoute(
    viewModel: AlarmViewModel
) {
    navigation(
        route = RootScreen.Alarm.route,
        startDestination = LeafScreen.Alarm.route
    ) {
        alarmScreen(
            viewModel = viewModel
        )
    }
}

private fun NavGraphBuilder.addTimerRoute(
    viewModel: TimerViewModel
) {
    navigation(
        route = RootScreen.Timer.route,
        startDestination = LeafScreen.Timer.route
    ) {
        timerScreen(
            viewModel = viewModel
        )
    }
}

private fun NavGraphBuilder.addStopwatchRoute(
    viewModel: StopwatchViewModel
) {
    navigation(
        route = RootScreen.Stopwatch.route,
        startDestination = LeafScreen.Stopwatch.route
    ) {
        stopwatchScreen(
            viewModel = viewModel
        )
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
