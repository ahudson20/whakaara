package com.whakaara.feature.alarm.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.whakaara.core.designsystem.theme.AlarmPreviewProvider
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.preferences.PreferencesState
import java.util.Calendar

@Composable
fun AlarmScreen(
    alarms: List<Alarm>,
    preferencesState: PreferencesState,
    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit,
    getInitialTimeToAlarm: (isEnabled: Boolean, time: Calendar) -> String,
    getTimeUntilAlarmFormatted: (date: Calendar) -> String
) {
    CardContainerSwipeToDismiss(
        alarms = alarms,
        timeFormat = preferencesState.preferences.timeFormat,
        delete = delete,
        disable = disable,
        enable = enable,
        reset = reset,
        getInitialTimeToAlarm = getInitialTimeToAlarm,
        getTimeUntilAlarmFormatted = getTimeUntilAlarmFormatted
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun AlarmScreenPreview(
    @PreviewParameter(AlarmPreviewProvider::class) alarm: Alarm
) {
    WhakaaraTheme {
        AlarmScreen(
            alarms = listOf(alarm),
            preferencesState = PreferencesState(),
            delete = {},
            disable = {},
            enable = {},
            reset = {},
            getInitialTimeToAlarm = { _, _ -> "" },
            getTimeUntilAlarmFormatted = { "" }
        )
    }
}
