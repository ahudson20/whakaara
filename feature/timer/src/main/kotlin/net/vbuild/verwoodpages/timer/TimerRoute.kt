package net.vbuild.verwoodpages.timer

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import net.vbuild.verwoodpages.timer.ui.TimerScreen

@Composable
fun TimerRoute(
    viewModel: TimerViewModel = hiltViewModel()
) {
    TimerScreen()
}
