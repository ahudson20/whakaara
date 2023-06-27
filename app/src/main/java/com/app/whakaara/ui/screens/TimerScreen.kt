package com.app.whakaara.ui.screens

import androidx.compose.runtime.Composable
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.clock.Timer

@Composable
fun TimerScreen(
    viewModel: MainViewModel,
) {
    Timer(
        formattedTime = viewModel.formattedTime,
        onStart = viewModel::start,
        onPause = viewModel::pause,
        onStop = viewModel::resetTimer
    )
}