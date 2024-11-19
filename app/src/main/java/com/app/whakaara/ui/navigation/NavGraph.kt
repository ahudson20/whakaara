package com.app.whakaara.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.app.whakaara.state.AlarmState
import com.whakaara.model.preferences.PreferencesState
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.events.AlarmEventCallbacks
import com.app.whakaara.state.events.StopwatchEventCallbacks
import com.app.whakaara.state.events.TimerEventCallbacks
import com.whakaara.core.LeafScreen
import com.whakaara.core.RootScreen
import com.whakaara.core.constants.GeneralConstants.ONBOARDING_ROUTE
import com.whakaara.model.preferences.Preferences
import com.whakaara.onboarding.navigation.onboardingScreen
import com.whakaara.stopwatch.navigation.stopwatchScreen
import com.whakaara.timer.navigation.timerScreen
import net.vbuild.verwoodpages.alarm.navigation.alarmScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    preferencesState: PreferencesState,
    alarmState: AlarmState,
    stopwatchState: StopwatchState,
//    timerState: TimerState,
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
            RootScreen.Alarm.route
//            BottomNavItem.Alarm.route
        }
    ) {
        addOnboardingRoute(navController)
        addAlarmRoute(navController)
        addTimerRoute(navController)
        addStopwatchRoute(navController)
//        composable(
//            route = ONBOARDING_ROUTE
//        ) {
//            OnboardingScreen(
//                navigateToHome = {
//                    navController.navigate(BottomNavItem.Alarm.route) {
//                        popUpTo(ONBOARDING_ROUTE) {
//                            inclusive = true
//                        }
//                    }
//                },
//                preferencesState = preferencesState,
//                updatePreferences = updatePreferences
//            )
//        }
//
//        composable(
//            route = BottomNavItem.Alarm.route,
//            deepLinks = listOf(
//                navDeepLink {
//                    uriPattern = DEEPLINK_ALARM
//                }
//            )
//        ) {
//            when (alarmState) {
//                is AlarmState.Loading -> Loading()
//                is AlarmState.Success ->
//                    AlarmScreen(
//                        // TODO: shift this into the VM?
//                        alarms = if (preferencesState.preferences.filteredAlarmList) {
//                            val (enabled, disabled) = alarmState.alarms.partition { it.isEnabled }
//
//                            val sortedEnabledList = with(enabled) {
//                                this.sortedBy { it.date.timeInMillis }
//                            }.toMutableList()
//
//                            (sortedEnabledList + disabled).toList()
//                        } else {
//                            alarmState.alarms
//                        },
//                        preferencesState = preferencesState,
//                        alarmEventCallbacks = alarmEventCallbacks
//                    )
//            }
//        }
//
//        composable(
//            route = BottomNavItem.Timer.route,
//            deepLinks = listOf(
//                navDeepLink {
//                    uriPattern = DEEPLINK_TIMER
//                }
//            )
//        ) {
//            TimerScreen(
////                timerState = timerState,
//                updateHours = timerEventCallbacks::updateHours,
//                updateMinutes = timerEventCallbacks::updateMinutes,
//                updateSeconds = timerEventCallbacks::updateSeconds,
//                startTimer = timerEventCallbacks::startTimer,
//                stopTimer = timerEventCallbacks::stopTimer,
//                pauseTimer = timerEventCallbacks::pauseTimer,
//                restartTimer = timerEventCallbacks::restartTimer,
//                timeFormat = preferencesState.preferences.timeFormat,
//                autoRestartTimer = preferencesState.preferences.autoRestartTimer
//            )
//        }
//
//        composable(
//            route = BottomNavItem.Stopwatch.route,
//            deepLinks = listOf(
//                navDeepLink {
//                    uriPattern = DEEPLINK_STOPWATCH
//                }
//            )
//        ) {
//            StopwatchScreen(
//                stopwatchState = stopwatchState,
//                onStart = stopwatchEventCallbacks::startStopwatch,
//                onPause = stopwatchEventCallbacks::pauseStopwatch,
//                onStop = stopwatchEventCallbacks::stopStopwatch,
//                onLap = stopwatchEventCallbacks::lapStopwatch
//            )
//        }
//    }
    }
}

private fun NavGraphBuilder.addAlarmRoute(navController: NavController) {
    navigation(
        route = RootScreen.Alarm.route,
        startDestination = LeafScreen.Alarm.route
    ) {
        alarmScreen()
    }
}

private fun NavGraphBuilder.addTimerRoute(navController: NavController) {
    navigation(
        route = RootScreen.Timer.route,
        startDestination = LeafScreen.Timer.route
    ) {
        timerScreen()
    }
}

private fun NavGraphBuilder.addStopwatchRoute(navController: NavController) {
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
        onboardingScreen()
    }
}
