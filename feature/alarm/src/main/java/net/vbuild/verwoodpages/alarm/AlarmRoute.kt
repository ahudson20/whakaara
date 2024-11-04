package net.vbuild.verwoodpages.alarm

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import net.vbuild.verwoodpages.alarm.ui.AlarmScreen

@Composable
fun AlarmRoute(
    viewModel: AlarmViewModel = hiltViewModel()
) {
    AlarmScreen()
}
