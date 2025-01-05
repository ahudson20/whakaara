package com.whakaara.feature.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whakaara.feature.timer.ui.TimerScreen

@Composable
fun TimerRoute(
    viewModel: TimerViewModel
) {
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    TimerScreen(
        timerState = timerState,
        updateHours = viewModel::updateInputHours,
        updateMinutes = viewModel::updateInputMinutes,
        updateSeconds = viewModel::updateInputSeconds,
        startTimer = viewModel::startTimer,
        stopTimer = viewModel::resetTimer,
        restartTimer = viewModel::restartTimer,
        pauseTimer = viewModel::pauseTimer,
        timeFormat = preferences.preferences.timeFormat
    )
}
