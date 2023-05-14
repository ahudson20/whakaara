package com.app.whakaara.ui.bottomsheet

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.whakaara.data.Alarm
import com.app.whakaara.utils.DateUtils
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.dokar.sheets.BottomSheetState
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier.padding(16.dp),
    alarm: Alarm,
    sheetState: BottomSheetState,
    reset: (alarm: Alarm) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var timePickerValue by remember { mutableStateOf<Hours>(FullHours(alarm.hour, alarm.minute)) }
    var isVibrationEnabled by remember(alarm.vibration) { mutableStateOf(alarm.vibration) }
    var isSnoozeEnabled by remember(alarm.isSnoozeEnabled) { mutableStateOf(alarm.isSnoozeEnabled) }
    var deleteAfterGoesOff by remember(alarm.deleteAfterGoesOff) { mutableStateOf(alarm.deleteAfterGoesOff) }
    var bottomText by remember {
        mutableStateOf(
            DateUtils.getInitialTimeToAlarm(
                isEnabled = alarm.isEnabled,
                hours = alarm.hour,
                minutes = alarm.minute
            )
        )
    }

    BackHandler(sheetState.visible) {
        coroutineScope.launch {
            sheetState.collapse()
        }
    }

    Column(
        modifier = modifier.fillMaxHeight()
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
            bottomText = bottomText
        )

        BottomSheetTimePicker(
            pickerValue = timePickerValue,
            updatePickerValue = { newValue ->
                timePickerValue = newValue
                bottomText = DateUtils.convertSecondsToHMm(
                    seconds = TimeUnit.MILLISECONDS.toSeconds(
                        DateUtils.getDifferenceFromCurrentTimeInMillis(
                            hours = newValue.hours,
                            minutes = newValue.minutes
                        )
                    )
                )
            }
        )

        BottomSheetAlarmDetails(
            isVibrationEnabled = isVibrationEnabled,
            updateIsVibrationEnabled = { newValue ->
                isVibrationEnabled = newValue
            },
            isSnoozeEnabled = isSnoozeEnabled,
            updateIsSnoozeEnabled = { newValue ->
                isSnoozeEnabled = newValue
            },
            deleteAfterGoesOff = deleteAfterGoesOff,
            updateDeleteAfterGoesOff = { newValue ->
                deleteAfterGoesOff = newValue
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BottomSheetContentPreview() {
    BottomSheetContent(
        alarm = Alarm(
            minute = 3,
            hour = 10,
            isEnabled = false,
            subTitle = "10:03 AM"
        ),
        sheetState = BottomSheetState(),
        reset = {}
    )
}