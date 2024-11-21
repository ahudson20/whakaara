package com.app.whakaara.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.state.events.PreferencesEventCallbacks
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.whakaara.core.RootScreen
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.PreferencesState
import com.whakaara.model.preferences.TimeFormat

@Composable
fun MainScreen(
    preferencesState: PreferencesState,
//    alarmState: AlarmState,
//    stopwatchState: StopwatchState,
//    timerState: TimerState,
//    alarmEventCallbacks: AlarmEventCallbacks,
//    timerEventCallbacks: TimerEventCallbacks,
//    stopwatchEventCallbacks: StopwatchEventCallbacks,
    preferencesEventCallbacks: PreferencesEventCallbacks
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentSelectedScreen by navController.currentScreenAsState()

    Scaffold(
        topBar = {
            if (!preferencesState.preferences.shouldShowOnboarding) {
                TopBar(
                    route = navBackStackEntry?.destination?.route.toString(), // TODO: change this
                    preferencesState = preferencesState,
                    preferencesEventCallbacks = preferencesEventCallbacks
                )
            }
        },
        bottomBar = {
            if (!preferencesState.preferences.shouldShowOnboarding) {
                BottomNavigation(
                    navController = navController,
                    currentSelectedScreen = currentSelectedScreen
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                shouldShowOnboarding = preferencesState.preferences.shouldShowOnboarding,
//                preferencesState = preferencesState,
//                alarmState = alarmState,
//                stopwatchState = stopwatchState,
//                timerState = timerState,
//                alarmEventCallbacks = alarmEventCallbacks,
//                timerEventCallbacks = timerEventCallbacks,
//                stopwatchEventCallbacks = stopwatchEventCallbacks,
//                updatePreferences = preferencesEventCallbacks::updatePreferences
            )
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun MainPreview() {
    WhakaaraTheme {
        MainScreen(
            preferencesState = PreferencesState(),
//            alarmState = AlarmState.Success(),
//            stopwatchState = StopwatchState(),
//            timerState = TimerState(),
//            alarmEventCallbacks = object : AlarmEventCallbacks {
//                override fun create(alarm: Alarm) {}
//
//                override fun delete(alarm: Alarm) {}
//
//                override fun disable(alarm: Alarm) {}
//
//                override fun enable(alarm: Alarm) {}
//
//                override fun reset(alarm: Alarm) {}
//
//                override fun getInitialTimeToAlarm(
//                    isEnabled: Boolean,
//                    time: Calendar
//                ): String {
//                    return ""
//                }
//
//                override fun getTimeUntilAlarmFormatted(date: Calendar): String {
//                    return ""
//                }
//            },
//            timerEventCallbacks = object : TimerEventCallbacks {
//                override fun updateHours(newValue: String) {}
//
//                override fun updateMinutes(newValue: String) {}
//
//                override fun updateSeconds(newValue: String) {}
//
//                override fun startTimer() {}
//
//                override fun stopTimer() {}
//
//                override fun pauseTimer() {}
//
//                override fun restartTimer(autoRestartTimer: Boolean) {}
//            },
//            stopwatchEventCallbacks = object : StopwatchEventCallbacks {
//                override fun startStopwatch() {}
//
//                override fun pauseStopwatch() {}
//
//                override fun stopStopwatch() {}
//
//                override fun lapStopwatch() {}
//            },
            preferencesEventCallbacks = object : PreferencesEventCallbacks {
                override fun updatePreferences(preferences: Preferences) {}

                override fun updateAllAlarmSubtitles(format: TimeFormat) {}

                override fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(
                    shouldEnableUpcomingAlarmNotification: Boolean
                ) {
                }
            }
        )
    }
}

@Stable
@Composable
private fun NavController.currentScreenAsState(): State<RootScreen> {
    val selectedItem = remember { mutableStateOf<RootScreen>(RootScreen.Alarm) }
    DisposableEffect(key1 = this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == RootScreen.Alarm.route } -> {
                    selectedItem.value = RootScreen.Alarm
                }

                destination.hierarchy.any { it.route == RootScreen.Stopwatch.route } -> {
                    selectedItem.value = RootScreen.Stopwatch
                }

                destination.hierarchy.any { it.route == RootScreen.Timer.route } -> {
                    selectedItem.value = RootScreen.Timer
                }

//                destination.hierarchy.any { it.route == RootScreen.Settings.route } -> {
//                    selectedItem.value = RootScreen.Settings
//                }
            }
        }
        addOnDestinationChangedListener(listener)
        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}
