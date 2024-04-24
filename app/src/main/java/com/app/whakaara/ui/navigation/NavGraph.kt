package com.app.whakaara.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.TimerState
import com.app.whakaara.state.events.AlarmEventCallbacks
import com.app.whakaara.state.events.StopwatchEventCallbacks
import com.app.whakaara.state.events.TimerEventCallbacks
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
    alarmEventCallbacks: AlarmEventCallbacks,
    timerEventCallbacks: TimerEventCallbacks,
    stopwatchEventCallbacks: StopwatchEventCallbacks,
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
                    alarms = if (preferencesState.preferences.filteredAlarmList) {
                        val (enabled, disabled) = alarmState.alarms.partition { it.isEnabled }

                        val sortedEnabledList = with(enabled) {
                            this.sortedBy { it.date.timeInMillis }
                        }.toMutableList()

                        (sortedEnabledList + disabled).toList()
                    } else {
                        alarmState.alarms
                    },
                    preferencesState = preferencesState,
                    delete = alarmEventCallbacks::delete,
                    disable = alarmEventCallbacks::disable,
                    enable = alarmEventCallbacks::enable,
                    reset = alarmEventCallbacks::reset,
                    create = alarmEventCallbacks::create
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
                updateHours = timerEventCallbacks::updateHours,
                updateMinutes = timerEventCallbacks::updateMinutes,
                updateSeconds = timerEventCallbacks::updateSeconds,
                startTimer = timerEventCallbacks::startTimer,
                stopTimer = timerEventCallbacks::stopTimer,
                pauseTimer = timerEventCallbacks::pauseTimer,
                restartTimer = timerEventCallbacks::restartTimer,
                is24HourFormat = preferencesState.preferences.is24HourFormat,
                autoRestartTimer = preferencesState.preferences.autoRestartTimer
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
                onStart = stopwatchEventCallbacks::startStopwatch,
                onPause = stopwatchEventCallbacks::pauseStopwatch,
                onStop = stopwatchEventCallbacks::stopStopwatch,
                onLap = stopwatchEventCallbacks::lapStopwatch
            )
        }
    }
}
