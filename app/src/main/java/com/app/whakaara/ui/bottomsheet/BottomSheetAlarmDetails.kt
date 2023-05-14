package com.app.whakaara.ui.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.whakaara.R

@Composable
fun BottomSheetAlarmDetails(
    modifier: Modifier = Modifier,
    isVibrationEnabled: Boolean,
    updateIsVibrationEnabled: (Boolean) -> Unit,
    isSnoozeEnabled: Boolean,
    updateIsSnoozeEnabled: (Boolean) -> Unit,
    deleteAfterGoesOff: Boolean,
    updateDeleteAfterGoesOff: (Boolean) -> Unit
) {
    Column {
        Row(
            modifier = modifier
                .padding(start = 10.dp, end = 10.dp, top = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.bottom_sheet_vibration_switch),
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                )
            )
            Switch(
                checked = isVibrationEnabled,
                onCheckedChange = {
                    updateIsVibrationEnabled(it)
                }
            )
        }

        Row(
            modifier = modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.bottom_sheet_snooze_switch),
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                )
            )
            Switch(
                checked = isSnoozeEnabled,
                onCheckedChange = {
                    updateIsSnoozeEnabled(it)
                }
            )
        }

        Row(
            modifier = modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.bottom_sheet_delete_switch),
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                )
            )
            Switch(
                checked = deleteAfterGoesOff,
                onCheckedChange = {
                    updateDeleteAfterGoesOff(it)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetAlarmDetailsPreview() {
    BottomSheetAlarmDetails(
        isVibrationEnabled = false,
        updateIsVibrationEnabled = {},
        isSnoozeEnabled = true,
        updateIsSnoozeEnabled = {},
        deleteAfterGoesOff = false,
        updateDeleteAfterGoesOff = { },
    )
}