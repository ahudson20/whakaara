package com.app.whakaara.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.TimerState
import com.app.whakaara.ui.navigation.BottomNavigation
import com.app.whakaara.ui.navigation.NavGraph
import com.app.whakaara.ui.navigation.TopBar
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme

@Composable
fun MainScreen(
    preferencesState: PreferencesState,
    alarmState: AlarmState,
    stopwatchState: StopwatchState,
    timerState: TimerState,
    create: (alarm: Alarm) -> Unit,
    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit,
    updateHours: (newValue: String) -> Unit,
    updateMinutes: (newValue: String) -> Unit,
    updateSeconds: (newValue: String) -> Unit,
    startTimer: () -> Unit,
    stopTimer: () -> Unit,
    pauseTimer: () -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    updatePreferences: (preferences: Preferences) -> Unit,
    updateAllAlarmSubtitles: (format: Boolean) -> Unit,
    filterAlarmList: (shouldFilter: Boolean) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        topBar = {
            if (!preferencesState.preferences.shouldShowOnboarding) {
                TopBar(
                    route = navBackStackEntry?.destination?.route.toString(),
                    preferencesState = preferencesState,
                    updatePreferences = updatePreferences,
                    updateAllAlarmSubtitles = updateAllAlarmSubtitles,
                    filterAlarmList = filterAlarmList
                )
            }
        },
        bottomBar = {
            if (!preferencesState.preferences.shouldShowOnboarding) {
                BottomNavigation(navController = navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavGraph(
                navController = navController,
                preferencesState = preferencesState,
                alarmState = alarmState,
                stopwatchState = stopwatchState,
                timerState = timerState,

                delete = delete,
                disable = disable,
                enable = enable,
                reset = reset,
                create = create,

                updateHours = updateHours,
                updateMinutes = updateMinutes,
                updateSeconds = updateSeconds,
                startTimer = startTimer,
                stopTimer = stopTimer,
                pauseTimer = pauseTimer,

                onStart = onStart,
                onPause = onPause,
                onStop = onStop,

                updatePreferences = updatePreferences
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
            alarmState = AlarmState.Success(),
            stopwatchState = StopwatchState(),
            timerState = TimerState(),
            create = {},
            delete = {},
            disable = {},
            enable = {},
            reset = {},
            updateHours = {},
            updateMinutes = {},
            updateSeconds = {},
            startTimer = {},
            stopTimer = {},
            pauseTimer = {},
            onStart = {},
            onPause = {},
            onStop = {},
            updatePreferences = {},
            updateAllAlarmSubtitles = {},
            filterAlarmList = {}
        )
    }
}
