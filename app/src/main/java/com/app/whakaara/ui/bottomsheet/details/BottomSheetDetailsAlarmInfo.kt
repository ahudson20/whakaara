package com.app.whakaara.ui.bottomsheet.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.app.whakaara.R
import com.app.whakaara.state.BooleanStateEvent
import com.app.whakaara.state.StringStateEvent
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space10
import com.app.whakaara.ui.theme.Spacings.space20
import com.app.whakaara.ui.theme.Spacings.space250
import com.app.whakaara.ui.theme.Spacings.spaceXxSmall
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_TITLE_MAX_CHARS

@Composable
fun BottomSheetDetailsAlarmInfo(
    modifier: Modifier = Modifier,
    updateIsVibrationEnabled: BooleanStateEvent,
    updateIsSnoozeEnabled: BooleanStateEvent,
    updateDeleteAfterGoesOff: BooleanStateEvent,
    updateTitle: StringStateEvent
) {
    val focusManager = LocalFocusManager.current

    Column {
        Row(
            modifier = modifier
                .padding(start = space10, end = space10, top = space20)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(id = R.string.bottom_sheet_vibration_switch)
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = stringResource(id = R.string.bottom_sheet_vibration_switch_sub_title)
                )
            }
            Switch(
                modifier = modifier.testTag("vibrate switch"),
                checked = updateIsVibrationEnabled.value,
                onCheckedChange = {
                    updateIsVibrationEnabled.onValueChange(it)
                }
            )
        }

        Row(
            modifier = modifier
                .padding(space10)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(id = R.string.bottom_sheet_snooze_switch)
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = stringResource(id = R.string.bottom_sheet_snooze_switch_sub_title)
                )
            }
            Switch(
                modifier = modifier.testTag("snooze switch"),
                checked = updateIsSnoozeEnabled.value,
                onCheckedChange = {
                    updateIsSnoozeEnabled.onValueChange(it)
                }
            )
        }

        Row(
            modifier = modifier
                .padding(space10)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(id = R.string.bottom_sheet_delete_switch)
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = stringResource(id = R.string.bottom_sheet_delete_switch_sub_title)
                )
            }
            Switch(
                modifier = modifier.testTag("delete switch"),
                checked = updateDeleteAfterGoesOff.value,
                onCheckedChange = {
                    updateDeleteAfterGoesOff.onValueChange(it)
                }
            )
        }

        Row(
            modifier = modifier
                .padding(space10)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.bottom_sheet_content_alarm_title))
            Column {
                TextField(
                    modifier = modifier.width(space250),
                    value = updateTitle.value,
                    onValueChange = { if ((it.length <= ALARM_TITLE_MAX_CHARS) && (!it.contains("\n"))) updateTitle.onValueChange(it) },
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
                        }
                    )
                )
                Text(
                    text = "${updateTitle.value.length} / $ALARM_TITLE_MAX_CHARS",
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = modifier
                        .align(Alignment.End)
                        .padding(top = spaceXxSmall)
                )
            }
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
private fun BottomSheetAlarmDetailsPreview() {
    WhakaaraTheme {
        BottomSheetDetailsAlarmInfo(
            updateIsVibrationEnabled = BooleanStateEvent(
                value = true
            ),
            updateIsSnoozeEnabled = BooleanStateEvent(
                value = true
            ),
            updateDeleteAfterGoesOff = BooleanStateEvent(
                value = false
            ),
            updateTitle = StringStateEvent(
                value = "title"
            )
        )
    }
}
