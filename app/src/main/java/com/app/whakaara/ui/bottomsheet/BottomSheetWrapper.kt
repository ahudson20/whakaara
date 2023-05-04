package com.app.whakaara.ui.bottomsheet

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.whakaara.data.Alarm
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetState

@Composable
fun BottomSheetWrapper(
    alarm: Alarm,
    state: BottomSheetState,
    modifier: Modifier = Modifier,
    cancel: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit
) {
    BottomSheet(
        state = state,
        modifier = modifier,
        skipPeeked = true,
    ) {
        BottomSheetContent(
            alarm = alarm,
            sheetState = state,
            cancel = cancel,
            enable = enable
        )
    }
}