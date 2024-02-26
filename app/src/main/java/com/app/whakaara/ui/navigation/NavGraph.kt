package com.app.whakaara.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.state.AlarmState
import com.app.whakaara.state.PreferencesState
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.TimerState
import com.app.whakaara.ui.screens.AlarmScreen
import com.app.whakaara.ui.screens.SettingsScreen
import com.app.whakaara.ui.screens.StopwatchScreen
import com.app.whakaara.ui.screens.TimerScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    preferencesState: PreferencesState,
    alarmState: AlarmState,
    stopwatchState: StopwatchState,
    timerState: TimerState
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Alarm.route
    ) {
        composable(
            route = BottomNavItem.Alarm.route
        ) {
            AlarmScreen(
                alarmState = alarmState,
                preferencesState = preferencesState,
                delete = viewModel::delete,
                disable = viewModel::disable,
                enable = viewModel::enable,
                reset = viewModel::reset
            )
        }

        composable(
            route = BottomNavItem.Timer.route
        ) {
            TimerScreen(
                timerState = timerState,
                updateHours = viewModel::updateInputHours,
                updateMinutes = viewModel::updateInputMinutes,
                updateSeconds = viewModel::updateInputSeconds,
                startTimer = viewModel::startTimer,
                stopTimer = viewModel::resetTimer,
                pauseTimer = viewModel::pauseTimer
            )
        }

        composable(
            route = BottomNavItem.Stopwatch.route
        ) {
            StopwatchScreen(
                stopwatchState = stopwatchState,
                onStart = viewModel::startStopwatch,
                onPause = viewModel::pauseStopwatch,
                onStop = viewModel::resetStopwatch
            )
        }

        composable(
            route = BottomNavItem.Settings.route
        ) {
            SettingsScreen(
                preferencesState = preferencesState,
                updatePreferences = viewModel::updatePreferences,
                updateAllAlarmSubtitles = viewModel::updateAllAlarmSubtitles
            )
        }
    }
}
