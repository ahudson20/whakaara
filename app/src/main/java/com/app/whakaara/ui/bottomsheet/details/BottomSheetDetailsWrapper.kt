package com.app.whakaara.ui.bottomsheet.details

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.app.whakaara.data.alarm.Alarm
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetState

@Composable
fun BottomSheetDetailsWrapper(
    alarm: Alarm,
    timeToAlarm: String,
    is24HourFormat: Boolean,
    state: BottomSheetState,
    reset: (alarm: Alarm) -> Unit
) {
    BottomSheet(
        backgroundColor = MaterialTheme.colorScheme.surface,
        state = state,
        skipPeeked = true
    ) {
        BottomSheetDetailsContent(
            alarm = alarm,
            timeToAlarm = timeToAlarm,
            is24HourFormat = is24HourFormat,
            sheetState = state,
            reset = reset
        )
    }
}
