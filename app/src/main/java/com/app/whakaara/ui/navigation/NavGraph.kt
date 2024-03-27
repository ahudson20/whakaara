package com.app.whakaara.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.TimerState
import com.app.whakaara.ui.loading.Loading
import com.app.whakaara.ui.screens.AlarmScreen
import com.app.whakaara.ui.screens.OnboardingScreen
import com.app.whakaara.ui.screens.StopwatchScreen
import com.app.whakaara.ui.screens.TimerScreen
import com.app.whakaara.utils.constants.GeneralConstants.DEEPLINK_ALARM
import com.app.whakaara.utils.constants.GeneralConstants.DEEPLINK_STOPWATCH
import com.app.whakaara.utils.constants.GeneralConstants.DEEPLINK_TIMER
import com.app.whakaara.utils.constants.GeneralConstants.ONBOARDING_ROUTE

@Composable
fun NavGraph(
    navController: NavHostController,
    preferencesState: PreferencesState,
    alarmState: AlarmState,
    stopwatchState: StopwatchState,
    timerState: TimerState,

    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit,
    create: (alarm: Alarm) -> Unit,

    updateHours: (newValue: String) -> Unit,
    updateMinutes: (newValue: String) -> Unit,
    updateSeconds: (newValue: String) -> Unit,
    startTimer: () -> Unit,
    stopTimer: () -> Unit,
    pauseTimer: () -> Unit,

    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,

    updatePreferences: (preferences: Preferences) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (preferencesState.preferences.shouldShowOnboarding) {
            ONBOARDING_ROUTE
        } else {
            BottomNavItem.Alarm.route
        }
    ) {
        composable(
            route = ONBOARDING_ROUTE
        ) {
            OnboardingScreen(
                navigateToHome = {
                    navController.navigate(BottomNavItem.Alarm.route) {
                        popUpTo(ONBOARDING_ROUTE) {
                            inclusive = true
                        }
                    }
                },
                preferencesState = preferencesState,
                updatePreferences = updatePreferences
            )
        }

        composable(
            route = BottomNavItem.Alarm.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DEEPLINK_ALARM
                }
            )
        ) {
            when (alarmState) {
                is AlarmState.Loading -> Loading()
                is AlarmState.Success -> AlarmScreen(
                    alarms = alarmState.alarms,
                    preferencesState = preferencesState,
                    delete = delete,
                    disable = disable,
                    enable = enable,
                    reset = reset,
                    create = create
                )
            }
        }

        composable(
            route = BottomNavItem.Timer.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DEEPLINK_TIMER
                }
            )
        ) {
            TimerScreen(
                timerState = timerState,
                updateHours = updateHours,
                updateMinutes = updateMinutes,
                updateSeconds = updateSeconds,
                startTimer = startTimer,
                stopTimer = stopTimer,
                pauseTimer = pauseTimer,
                is24HourFormat = preferencesState.preferences.is24HourFormat
            )
        }

        composable(
            route = BottomNavItem.Stopwatch.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = DEEPLINK_STOPWATCH
                }
            )
        ) {
            StopwatchScreen(
                stopwatchState = stopwatchState,
                onStart = onStart,
                onPause = onPause,
                onStop = onStop
            )
        }
    }
}
