package com.app.whakaara.ui.bottomsheet

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.whakaara.R
import com.app.whakaara.data.Alarm
import com.app.whakaara.utils.DateUtils
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import com.dokar.sheets.BottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun BottomSheetContent(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    sheetState: BottomSheetState,
    reset: (alarm: Alarm) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var timePickerValue by remember { mutableStateOf<Hours>(FullHours(alarm.hour, alarm.minute)) }
    var isVibrationEnabled by remember(alarm.vibration) { mutableStateOf(alarm.vibration) }
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
            context = context,
            reset = reset,
            pickerValue = timePickerValue,
            isVibrationEnabled = isVibrationEnabled,
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

        Column {
            Text(text = alarm.title ?: "title")

            Text(text = alarm.subTitle ?: "subtitle")

            Row(
                modifier = modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "isVibration: " + alarm.vibration.toString())
                Switch(
                    checked = isVibrationEnabled,
                    onCheckedChange = {
                        isVibrationEnabled = !isVibrationEnabled
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomSheetTimePicker(
    modifier: Modifier = Modifier,
    pickerValue: Hours,
    updatePickerValue: (Hours) -> Unit
) {
    HoursNumberPicker(
        dividersColor = MaterialTheme.colors.primary,
        leadingZero = false,
        value = pickerValue,
        onValueChange = {
            updatePickerValue(it)
        },
        hoursDivider = {
            Text(
                modifier = modifier.size(24.dp),
                textAlign = TextAlign.Center,
                text = ":"
            )
        }
    )
}

@Composable
private fun BottomSheetTopBar(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope,
    sheetState: BottomSheetState,
    alarm: Alarm,
    context: Context,
    reset: (alarm: Alarm) -> Unit,
    pickerValue: Hours,
    isVibrationEnabled: Boolean,
    bottomText: String
) {
    Row(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ClickableText(
            text = AnnotatedString(stringResource(id = R.string.bottom_sheet_close_button)),
            onClick = {
                coroutineScope.launch {
                    sheetState.collapse()
                }
            }
        )
        BottomSheetTitle(
            title = alarm.title,
            bottomText = bottomText
        )
        ClickableText(
            text = AnnotatedString(stringResource(id = R.string.bottom_sheet_save_button)),
            onClick = {
                coroutineScope.launch {
                    reset(
                        alarm.copy(
                            hour = pickerValue.hours,
                            minute = pickerValue.minutes,
                            isEnabled = true,
                            vibration = isVibrationEnabled
                        )
                    )
                    Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show()
                    sheetState.collapse()
                }
            }
        )
    }
}

@Composable
private fun BottomSheetTitle(
    title: String?,
    bottomText: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (!title.isNullOrBlank()) {
                title.toString()
            } else {
                stringResource(id = R.string.bottom_sheet_title)
            }
        )

        Text(
            text = bottomText
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetTimePickerPreview() {
    BottomSheetTimePicker(
        pickerValue = FullHours(
            hours = 12,
            minutes = 12
        ),
        updatePickerValue = {}
    )
}

@Preview(showBackground = true)
@Composable
fun BottomSheetTopBarPreview() {
    BottomSheetTopBar(
        coroutineScope = rememberCoroutineScope(),
        sheetState = BottomSheetState(),
        alarm = Alarm(
            minute = 3,
            hour = 10,
            isEnabled = false,
            subTitle = "10:03 AM"
        ),
        context = LocalContext.current,
        reset = {},
        pickerValue = FullHours(
            hours = 12,
            minutes = 12
        ),
        isVibrationEnabled = true,
        bottomText = "bottomText"
    )
}

@Preview(showBackground = true)
@Composable
fun BottomSheetTitlePreview() {
    BottomSheetTitle(
        title = "title",
        bottomText = "Off"
    )
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