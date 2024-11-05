package net.vbuild.verwoodpages.stopwatch

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import net.vbuild.verwoodpages.stopwatch.ui.StopwatchScreen

@Composable
fun StopwatchRoute(
    viewModel: StopwatchViewModel = hiltViewModel()
) {
    StopwatchScreen()
}
