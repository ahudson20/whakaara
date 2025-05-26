package com.whakaara.feature.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
        timeFormat = preferences.preferences.timeFormat
    )
}
