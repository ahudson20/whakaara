package com.whakaara.feature.stopwatch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whakaara.feature.stopwatch.ui.StopwatchScreen

@Composable
fun StopwatchRoute(
    viewModel: StopwatchViewModel
) {
    val stopwatchState by viewModel.stopwatchState.collectAsStateWithLifecycle()

    StopwatchScreen(
        stopwatchState = stopwatchState,
        onStart = viewModel::startStopwatch,
        onPause = viewModel::pauseStopwatch,
        onStop = viewModel::resetStopwatch,
        onLap = viewModel::lapStopwatch
    )
}
