package com.app.whakaara.ui.bottomsheet

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.app.whakaara.data.alarm.Alarm
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetState

@Composable
fun BottomSheetWrapper(
    alarm: Alarm,
    timeToAlarm: String,
    state: BottomSheetState,
    reset: (alarm: Alarm) -> Unit
) {
    BottomSheet(
        backgroundColor = MaterialTheme.colorScheme.surface,
        state = state,
        skipPeeked = true
    ) {
        BottomSheetContent(
            alarm = alarm,
            timeToAlarm = timeToAlarm,
            sheetState = state,
            reset = reset
        )
    }
}
