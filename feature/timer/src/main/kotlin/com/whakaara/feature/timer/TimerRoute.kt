package com.whakaara.feature.timer

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.whakaara.feature.timer.ui.TimerScreen

@Composable
fun TimerRoute(
    viewModel: TimerViewModel = hiltViewModel()
) {
    TimerScreen()
}
