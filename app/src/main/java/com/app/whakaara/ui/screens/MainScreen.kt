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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.state.events.PreferencesEventCallbacks
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.whakaara.core.RootScreen
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.alarm.AlarmViewModel
import com.whakaara.feature.stopwatch.StopwatchViewModel
import com.whakaara.feature.timer.TimerViewModel
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.PreferencesState
import com.whakaara.model.preferences.TimeFormat

@Composable
fun MainScreen(
    preferencesState: PreferencesState,
    preferencesEventCallbacks: PreferencesEventCallbacks,
    stopwatchViewModel: StopwatchViewModel,
    timerViewModel: TimerViewModel,
    alarmViewModel: AlarmViewModel
) {
    val navController = rememberNavController()
    val currentSelectedScreen by navController.currentScreenAsState()
    val currentRoute by navController.currentRouteAsState()

    Scaffold(
        topBar = {
            if (!preferencesState.preferences.shouldShowOnboarding) {
                TopBar(
                    route = currentRoute,
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
                alarmViewModel = alarmViewModel,
                timerViewModel = timerViewModel,
                stopwatchViewModel = stopwatchViewModel
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
            preferencesEventCallbacks = object : PreferencesEventCallbacks {
                override fun updatePreferences(preferences: Preferences) {}

                override fun updateAllAlarmSubtitles(format: TimeFormat) {}

                override fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(
                    shouldEnableUpcomingAlarmNotification: Boolean
                ) {
                }
            },
            alarmViewModel = hiltViewModel(),
            stopwatchViewModel = hiltViewModel(),
            timerViewModel = hiltViewModel()
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

@Stable
@Composable
private fun NavController.currentRouteAsState(): State<String?> {
    val selectedItem = remember { mutableStateOf<String?>(null) }
    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            selectedItem.value = destination.route
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}
