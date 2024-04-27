package com.app.whakaara.ui.bottomsheet.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
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
import com.app.whakaara.state.UpdateBottomSheetDetailsAlarmInfo
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space10
import com.app.whakaara.ui.theme.Spacings.space20
import com.app.whakaara.ui.theme.Spacings.space250
import com.app.whakaara.ui.theme.Spacings.spaceXxSmall
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.constants.GeneralConstants.DAYS_OF_WEEK
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_TITLE_MAX_CHARS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDetailsAlarmInfo(
    modifier: Modifier = Modifier,
    updateBottomSheetDetailsAlarmInfo: UpdateBottomSheetDetailsAlarmInfo
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
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
                modifier = Modifier.testTag("vibrate switch"),
                checked = updateBottomSheetDetailsAlarmInfo.updateIsVibrationEnabled.value,
                onCheckedChange = {
                    updateBottomSheetDetailsAlarmInfo.updateIsVibrationEnabled.onValueChange(it)
                }
            )
        }

        Row(
            modifier = Modifier
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
                modifier = Modifier.testTag("snooze switch"),
                checked = updateBottomSheetDetailsAlarmInfo.updateIsSnoozeEnabled.value,
                onCheckedChange = {
                    updateBottomSheetDetailsAlarmInfo.updateIsSnoozeEnabled.onValueChange(it)
                }
            )
        }

        Row(
            modifier = Modifier
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
                modifier = Modifier.testTag("delete switch"),
                checked = updateBottomSheetDetailsAlarmInfo.updateDeleteAfterGoesOff.value,
                enabled = !updateBottomSheetDetailsAlarmInfo.updateRepeatDaily.value && updateBottomSheetDetailsAlarmInfo.updateCheckedList.value.isEmpty(),
                onCheckedChange = {
                    updateBottomSheetDetailsAlarmInfo.updateDeleteAfterGoesOff.onValueChange(it)
                }
            )
        }

        Row(
            modifier = Modifier
                .padding(all = space10)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(id = R.string.bottom_sheet_repeat_switch_title)
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = stringResource(id = R.string.bottom_sheet_repeat_switch_sub_title)
                )
            }
            Switch(
                modifier = Modifier.testTag("repeat alarm switch"),
                checked = updateBottomSheetDetailsAlarmInfo.updateRepeatDaily.value,
                enabled = !updateBottomSheetDetailsAlarmInfo.updateDeleteAfterGoesOff.value && updateBottomSheetDetailsAlarmInfo.updateCheckedList.value.isEmpty(),
                onCheckedChange = {
                    updateBottomSheetDetailsAlarmInfo.updateRepeatDaily.onValueChange(it)
                }
            )
        }

        Row(
            modifier = Modifier
                .padding(space10)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(id = R.string.bottom_sheet_custom_alarm_days_title)
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = stringResource(id = R.string.bottom_sheet_custom_alarm_days_sub_title)
                )
                MultiChoiceSegmentedButtonRow {
                    DAYS_OF_WEEK.forEachIndexed { index, label ->
                        SegmentedButton(
                            modifier = Modifier.testTag("segmentedButton"),
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = DAYS_OF_WEEK.size),
                            onCheckedChange = {
                                updateBottomSheetDetailsAlarmInfo.updateCheckedList.onValueChange(index)
                            },
                            checked = index in updateBottomSheetDetailsAlarmInfo.updateCheckedList.value,
                            enabled = !updateBottomSheetDetailsAlarmInfo.updateDeleteAfterGoesOff.value && !updateBottomSheetDetailsAlarmInfo.updateRepeatDaily.value,
                            colors = SegmentedButtonDefaults.colors().copy(
                                disabledInactiveContainerColor = MaterialTheme.colorScheme.surface,
                                disabledInactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                disabledInactiveBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )
                        ) {
                            Text(label)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(space10)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.bottom_sheet_content_alarm_title))
            Column {
                TextField(
                    modifier = Modifier.width(space250),
                    value = updateBottomSheetDetailsAlarmInfo.updateTitle.value,
                    onValueChange = { if ((it.length <= ALARM_TITLE_MAX_CHARS) && (!it.contains("\n"))) updateBottomSheetDetailsAlarmInfo.updateTitle.onValueChange(it) },
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
                    text = stringResource(id = R.string.bottom_sheet_title_characters, updateBottomSheetDetailsAlarmInfo.updateTitle.value.length, ALARM_TITLE_MAX_CHARS),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
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
            updateBottomSheetDetailsAlarmInfo = UpdateBottomSheetDetailsAlarmInfo()
        )
    }
}
