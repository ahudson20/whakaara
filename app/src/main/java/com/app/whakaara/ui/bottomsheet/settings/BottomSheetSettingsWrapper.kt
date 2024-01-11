package com.app.whakaara.ui.bottomsheet.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetState

@Composable
fun BottomSheetSettingsWrapper(
    state: BottomSheetState
) {
    BottomSheet(
        backgroundColor = MaterialTheme.colorScheme.surface,
        state = state,
        skipPeeked = true
    ) {
        Text(text = "~~settings bottom sheet~~")
    }
}
