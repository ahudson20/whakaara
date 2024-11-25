package com.whakaara.feature.alarm

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.whakaara.feature.alarm.ui.AlarmScreen

@Composable
fun AlarmRoute(
    viewModel: AlarmViewModel = hiltViewModel()
) {
    AlarmScreen()
}
