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
import com.app.whakaara.state.events.AlarmEventCallbacks
import com.app.whakaara.state.events.PreferencesEventCallbacks
import com.app.whakaara.state.events.StopwatchEventCallbacks
import com.app.whakaara.state.events.TimerEventCallbacks
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
    alarmEventCallbacks: AlarmEventCallbacks,
    timerEventCallbacks: TimerEventCallbacks,
    stopwatchEventCallbacks: StopwatchEventCallbacks,
    preferencesEventCallbacks: PreferencesEventCallbacks
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        topBar = {
            if (!preferencesState.preferences.shouldShowOnboarding) {
                TopBar(
                    route = navBackStackEntry?.destination?.route.toString(),
                    preferencesState = preferencesState,
                    preferencesEventCallbacks = preferencesEventCallbacks
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
                alarmEventCallbacks = alarmEventCallbacks,
                timerEventCallbacks = timerEventCallbacks,
                stopwatchEventCallbacks = stopwatchEventCallbacks,
                updatePreferences = preferencesEventCallbacks::updatePreferences
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
            alarmEventCallbacks = object : AlarmEventCallbacks {
                override fun create(alarm: Alarm) {}
                override fun delete(alarm: Alarm) {}
                override fun disable(alarm: Alarm) {}
                override fun enable(alarm: Alarm) {}
                override fun reset(alarm: Alarm) {}
            },
            timerEventCallbacks = object : TimerEventCallbacks {
                override fun updateHours(newValue: String) {}
                override fun updateMinutes(newValue: String) {}
                override fun updateSeconds(newValue: String) {}
                override fun startTimer() {}
                override fun stopTimer() {}
                override fun pauseTimer() {}
                override fun restartTimer(autoRestartTimer: Boolean) {}
            },
            stopwatchEventCallbacks = object : StopwatchEventCallbacks {
                override fun startStopwatch() {}
                override fun pauseStopwatch() {}
                override fun stopStopwatch() {}
                override fun lapStopwatch() {}
            },
            preferencesEventCallbacks = object : PreferencesEventCallbacks {
                override fun updatePreferences(preferences: Preferences) {}
                override fun updateAllAlarmSubtitles(format: Boolean) {}
                override fun updateCurrentAlarmsToAddOrRemoveUpcomingAlarmNotification(
                    shouldEnableUpcomingAlarmNotification: Boolean
                ) {}
            }
        )
    }
}
