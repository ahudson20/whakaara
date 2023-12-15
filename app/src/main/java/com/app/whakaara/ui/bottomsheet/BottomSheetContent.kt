package com.app.whakaara.ui.bottomsheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.state.BooleanStateEvent
import com.app.whakaara.state.HoursUpdateEvent
import com.app.whakaara.state.StringStateEvent
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.DateUtils.Companion.getTimeUntilAlarmFormatted
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.dokar.sheets.BottomSheetState
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    timeToAlarm: String,
    is24HourFormat: Boolean,
    sheetState: BottomSheetState,
    reset: (alarm: Alarm) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    var timePickerValue by remember { mutableStateOf<Hours>(FullHours(alarm.date.get(Calendar.HOUR_OF_DAY), alarm.date.get(Calendar.MINUTE))) }
    var isVibrationEnabled by remember(alarm.vibration) { mutableStateOf(alarm.vibration) }
    var isSnoozeEnabled by remember(alarm.isSnoozeEnabled) { mutableStateOf(alarm.isSnoozeEnabled) }
    var deleteAfterGoesOff by remember(alarm.deleteAfterGoesOff) { mutableStateOf(alarm.deleteAfterGoesOff) }
    var title by remember(alarm.title) { mutableStateOf(alarm.title) }
    var bottomText by remember { mutableStateOf(timeToAlarm) }

    BackHandler(sheetState.visible) {
        coroutineScope.launch {
            sheetState.collapse()
        }
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(spaceMedium)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            }
    ) {
        BottomSheetTopBar(
            coroutineScope = coroutineScope,
            sheetState = sheetState,
            alarm = alarm,
            reset = reset,
            pickerValue = timePickerValue,
            isVibrationEnabled = isVibrationEnabled,
            isSnoozeEnabled = isSnoozeEnabled,
            deleteAfterGoesOff = deleteAfterGoesOff,
            bottomText = bottomText,
            title = title,
            is24HourFormat = is24HourFormat
        )

        BottomSheetTimePicker(
            updatePickerValue = HoursUpdateEvent(
                value = timePickerValue,
                onValueChange = { newValue ->
                    timePickerValue = newValue
                    bottomText = getTimeUntilAlarmFormatted(
                        date = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, newValue.hours)
                            set(Calendar.MINUTE, newValue.minutes)
                        }
                    )
                }
            )
        )

        BottomSheetAlarmDetails(
            updateIsVibrationEnabled = BooleanStateEvent(
                value = isVibrationEnabled,
                onValueChange = { newValue ->
                    isVibrationEnabled = newValue
                }
            ),
            updateIsSnoozeEnabled = BooleanStateEvent(
                value = isSnoozeEnabled,
                onValueChange = { newValue ->
                    isSnoozeEnabled = newValue
                }
            ),
            updateDeleteAfterGoesOff = BooleanStateEvent(
                value = deleteAfterGoesOff,
                onValueChange = { newValue ->
                    deleteAfterGoesOff = newValue
                }
            ),
            updateTitle = StringStateEvent(
                value = title,
                onValueChange = { newValue ->
                    title = newValue
                }
            )
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun BottomSheetContentPreview() {
    WhakaaraTheme {
        BottomSheetContent(
            alarm = Alarm(
                date = Calendar.getInstance(),
                isEnabled = false,
                subTitle = "10:03 AM"
            ),
            timeToAlarm = "timeToAlarm",
            is24HourFormat = true,
            sheetState = BottomSheetState(),
            reset = {}
        )
    }
}
