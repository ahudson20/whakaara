package com.app.whakaara.ui.screens

import androidx.compose.runtime.Composable
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.state.AlarmState
import com.app.whakaara.ui.card.CardContainerSwipeToDismiss
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import java.util.Calendar

@Composable
fun AlarmScreen(
    alarmState: AlarmState,
    is24HourFormat: Boolean,
    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit
) {
    CardContainerSwipeToDismiss(
        alarms = alarmState,
        is24HourFormat = is24HourFormat,
        delete = delete,
        disable = disable,
        enable = enable,
        reset = reset
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun AlarmScreenPreview() {
    WhakaaraTheme {
        AlarmScreen(
            alarmState = AlarmState(
                alarms = listOf(
                    Alarm(
                        date = Calendar.getInstance().apply {
                            set(Calendar.YEAR, 2023)
                            set(Calendar.DAY_OF_MONTH, 13)
                            set(Calendar.MONTH, 6)
                            set(Calendar.HOUR_OF_DAY, 14)
                            set(Calendar.MINUTE, 34)
                            set(Calendar.SECOND, 0)
                        },
                        title = "First Alarm Title",
                        subTitle = "14:34 PM"
                    )
                )
            ),
            is24HourFormat = true,
            delete = {},
            disable = {},
            enable = {},
            reset = {}
        )
    }
}
