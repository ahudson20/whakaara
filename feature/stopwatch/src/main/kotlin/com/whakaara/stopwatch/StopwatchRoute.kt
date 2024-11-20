package com.whakaara.stopwatch

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.whakaara.stopwatch.ui.StopwatchScreen

@Composable
fun StopwatchRoute(
    viewModel: StopwatchViewModel = hiltViewModel()
) {
    StopwatchScreen()
}
