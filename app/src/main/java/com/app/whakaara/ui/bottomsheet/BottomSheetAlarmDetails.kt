package com.app.whakaara.ui.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.whakaara.R
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_TITLE_MAX_CHARS

@Composable
fun BottomSheetAlarmDetails(
    modifier: Modifier = Modifier,
    isVibrationEnabled: Boolean,
    updateIsVibrationEnabled: (Boolean) -> Unit,
    isSnoozeEnabled: Boolean,
    updateIsSnoozeEnabled: (Boolean) -> Unit,
    deleteAfterGoesOff: Boolean,
    updateDeleteAfterGoesOff: (Boolean) -> Unit,
    title: String,
    updateTitle: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

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
                ),
                fontSize = 16.sp
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
                ),
                fontSize = 16.sp
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
                ),
                fontSize = 16.sp
            )
            Switch(
                checked = deleteAfterGoesOff,
                onCheckedChange = {
                    updateDeleteAfterGoesOff(it)
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
                text = stringResource(id = R.string.bottom_sheet_content_alarm_title),
                style = TextStyle(
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 16.sp
            )
            Column {
                TextField(
                    modifier = modifier.width(150.dp),
                    value = title,
                    onValueChange = { if ((it.length <= ALARM_TITLE_MAX_CHARS) && (!it.contains("\n"))) updateTitle(it) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }),
                )
                Text(
                    text = "${title.length} / $ALARM_TITLE_MAX_CHARS",
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
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
        updateDeleteAfterGoesOff = {},
        title = "Alarm",
        updateTitle = {}
    )
}