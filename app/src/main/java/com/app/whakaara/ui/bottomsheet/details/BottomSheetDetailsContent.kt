package com.app.whakaara.ui.bottomsheet.details

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.app.whakaara.R
import com.app.whakaara.state.BooleanStateEvent
import com.app.whakaara.state.HoursUpdateEvent
import com.app.whakaara.state.ListStateEvent
import com.app.whakaara.state.StringStateEvent
import com.app.whakaara.state.UpdateBottomSheetDetailsAlarmInfo
import com.app.whakaara.ui.theme.AlarmPreviewProvider
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space10
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utility.DateUtils.Companion.getAlarmTimeFormatted
import com.app.whakaara.utility.DateUtils.Companion.getTimeUntilAlarmFormatted
import com.app.whakaara.utility.GeneralUtils.Companion.showToast
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.dokar.sheets.BottomSheetState
import com.whakaara.model.alarm.Alarm
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun BottomSheetDetailsContent(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    timeToAlarm: String,
    is24HourFormat: Boolean,
    sheetState: BottomSheetState,
    reset: (alarm: Alarm) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var timePickerValue by remember {
        mutableStateOf<Hours>(
            FullHours(alarm.date.get(Calendar.HOUR_OF_DAY), alarm.date.get(Calendar.MINUTE)),
        )
    }
    var isVibrationEnabled by remember(alarm.vibration) { mutableStateOf(alarm.vibration) }
    var isSnoozeEnabled by remember(alarm.isSnoozeEnabled) { mutableStateOf(alarm.isSnoozeEnabled) }
    var deleteAfterGoesOff by remember(alarm.deleteAfterGoesOff) { mutableStateOf(alarm.deleteAfterGoesOff) }
    var isRepeatDaily by remember(alarm.repeatDaily) { mutableStateOf(alarm.repeatDaily) }
    val checkedList = remember(alarm.daysOfWeek) { alarm.daysOfWeek.toMutableStateList() }
    var title by remember(alarm.title) { mutableStateOf(alarm.title) }
    var bottomText by remember { mutableStateOf(timeToAlarm) }
    val context = LocalContext.current

    BackHandler(sheetState.visible) {
        coroutineScope.launch {
            sheetState.collapse()
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .padding(spaceMedium)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                        },
                    )
                },
    ) {
        BottomSheetDetailsTopBar(
            bottomText = bottomText,
            title = title,
        )

        BottomSheetTimePicker(
            updatePickerValue =
                HoursUpdateEvent(
                    value = timePickerValue,
                    onValueChange = { newValue ->
                        timePickerValue = newValue
                        bottomText =
                            context.getTimeUntilAlarmFormatted(
                                date =
                                    Calendar.getInstance().apply {
                                        set(Calendar.HOUR_OF_DAY, newValue.hours)
                                        set(Calendar.MINUTE, newValue.minutes)
                                    },
                            )
                    },
                ),
        )

        BottomSheetDetailsAlarmInfo(
            updateBottomSheetDetailsAlarmInfo =
                UpdateBottomSheetDetailsAlarmInfo(
                    updateIsVibrationEnabled =
                        BooleanStateEvent(
                            value = isVibrationEnabled,
                            onValueChange = { newValue ->
                                isVibrationEnabled = newValue
                            },
                        ),
                    updateIsSnoozeEnabled =
                        BooleanStateEvent(
                            value = isSnoozeEnabled,
                            onValueChange = { newValue ->
                                isSnoozeEnabled = newValue
                            },
                        ),
                    updateDeleteAfterGoesOff =
                        BooleanStateEvent(
                            value = deleteAfterGoesOff,
                            onValueChange = { newValue ->
                                deleteAfterGoesOff = newValue
                            },
                        ),
                    updateRepeatDaily =
                        BooleanStateEvent(
                            value = isRepeatDaily,
                            onValueChange = { newValue ->
                                isRepeatDaily = newValue
                            },
                        ),
                    updateCheckedList =
                        ListStateEvent(
                            value = checkedList,
                            onValueChange = { newValue ->
                                if (newValue in checkedList) {
                                    checkedList.remove(newValue)
                                } else {
                                    checkedList.add(newValue)
                                }
                            },
                        ),
                    updateTitle =
                        StringStateEvent(
                            value = title,
                            onValueChange = { newValue ->
                                title = newValue
                            },
                        ),
                ),
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        sheetState.collapse()
                    }
                },
            ) {
                Text(text = stringResource(id = R.string.bottom_sheet_close_button))
            }
            Spacer(modifier = Modifier.width(space10))
            Button(
                onClick = {
                    coroutineScope.launch {
                        reset(
                            alarm.copy(
                                date =
                                    alarm.date.apply {
                                        set(Calendar.HOUR_OF_DAY, timePickerValue.hours)
                                        set(Calendar.MINUTE, timePickerValue.minutes)
                                    },
                                isEnabled = true,
                                vibration = isVibrationEnabled,
                                isSnoozeEnabled = isSnoozeEnabled,
                                deleteAfterGoesOff = deleteAfterGoesOff,
                                repeatDaily = isRepeatDaily,
                                daysOfWeek = checkedList,
                                title = title,
                                subTitle =
                                    getAlarmTimeFormatted(
                                        date = alarm.date,
                                        is24HourFormatEnabled = is24HourFormat,
                                    ),
                            ),
                        )
                        context.showToast(message = context.getString(R.string.bottom_sheet_save_button))
                        sheetState.collapse()
                    }
                },
            ) {
                Text(text = stringResource(id = R.string.bottom_sheet_save_button))
            }
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun BottomSheetContentPreview(
    @PreviewParameter(AlarmPreviewProvider::class) alarm: Alarm,
) {
    WhakaaraTheme {
        BottomSheetDetailsContent(
            alarm = alarm,
            timeToAlarm = "timeToAlarm",
            is24HourFormat = true,
            sheetState = BottomSheetState(),
            reset = {},
        )
    }
}
