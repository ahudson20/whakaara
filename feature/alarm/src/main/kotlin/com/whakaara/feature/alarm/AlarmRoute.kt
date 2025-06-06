package com.whakaara.feature.alarm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.whakaara.core.designsystem.loading.Loading
import com.whakaara.feature.alarm.ui.AlarmScreen
import com.whakaara.model.alarm.AlarmState

@Composable
fun AlarmRoute(
    viewModel: AlarmViewModel
) {
    val alarmState by viewModel.alarmState.collectAsStateWithLifecycle()
    val preferencesState by viewModel.preferencesState.collectAsStateWithLifecycle()

    when (val state = alarmState) {
        is AlarmState.Loading -> {
            Loading()
        }

        is AlarmState.Success -> {
            AlarmScreen(
                alarms = state.alarms,
                preferencesState = preferencesState,
                delete = viewModel::delete,
                disable = viewModel::disable,
                enable = viewModel::enable,
                reset = viewModel::reset,
                getInitialTimeToAlarm = viewModel::getInitialTimeToAlarm,
                getTimeUntilAlarmFormatted = viewModel::getTimeUntilAlarmFormatted
            )
        }
    }
}
