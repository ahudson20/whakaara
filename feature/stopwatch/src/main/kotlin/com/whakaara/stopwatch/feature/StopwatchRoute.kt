package com.whakaara.stopwatch.feature

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.whakaara.stopwatch.feature.ui.StopwatchScreen

@Composable
fun StopwatchRoute(
    viewModel: StopwatchViewModel = hiltViewModel()
) {
    StopwatchScreen()
}
