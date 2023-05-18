package com.app.whakaara.ui.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.whakaara.R
import com.app.whakaara.data.Alarm
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.GeneralUtils
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.dokar.sheets.BottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BottomSheetTopBar(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope,
    sheetState: BottomSheetState,
    alarm: Alarm,
    reset: (alarm: Alarm) -> Unit,
    pickerValue: Hours,
    isVibrationEnabled: Boolean,
    isSnoozeEnabled: Boolean,
    deleteAfterGoesOff: Boolean,
    bottomText: String,
    title: String
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {
                coroutineScope.launch {
                    sheetState.collapse()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "close"
            )
        }

        BottomSheetTitle(
            title = title,
            bottomText = bottomText
        )

        IconButton(
            onClick = {
                coroutineScope.launch {
                    reset(
                        alarm.copy(
                            hour = pickerValue.hours,
                            minute = pickerValue.minutes,
                            isEnabled = true,
                            vibration = isVibrationEnabled,
                            isSnoozeEnabled = isSnoozeEnabled,
                            deleteAfterGoesOff = deleteAfterGoesOff,
                            title = title,
                            subTitle = DateUtils.alarmTimeTo24HourFormat(
                                hour = pickerValue.hours,
                                minute = pickerValue.minutes
                            )
                        )
                    )
                    GeneralUtils.showToast(title = context.getString(R.string.bottom_sheet_save_button), context = context)
                    sheetState.collapse()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "close"
            )
        }
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
            modifier = Modifier.padding(bottom = 4.dp),
            text = if (!title.isNullOrBlank()) {
                title.toString()
            } else {
                stringResource(id = R.string.bottom_sheet_title)
            },
            style = TextStyle(
                fontSize = 20.sp
            ),
        )

        Text(
            text = bottomText,
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray
            )
        )
    }
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
        reset = {},
        pickerValue = FullHours(
            hours = 12,
            minutes = 12
        ),
        isVibrationEnabled = true,
        isSnoozeEnabled = true,
        deleteAfterGoesOff = false,
        bottomText = "bottomText",
        title = "title"
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