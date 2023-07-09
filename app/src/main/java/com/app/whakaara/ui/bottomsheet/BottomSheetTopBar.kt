package com.app.whakaara.ui.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.GeneralUtils
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.dokar.sheets.BottomSheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

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
            .height(IntrinsicSize.Min)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = modifier.fillMaxHeight(),
            onClick = {
                coroutineScope.launch {
                    sheetState.collapse()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(id = R.string.bottom_sheet_icon_cancel_content_description)
            )
        }

        BottomSheetTitle(
            title = title,
            bottomText = bottomText
        )

        IconButton(
            modifier = modifier.fillMaxHeight(),
            onClick = {
                coroutineScope.launch {
                    reset(
                        alarm.copy(
                            date = alarm.date.apply {
                                set(Calendar.HOUR_OF_DAY, pickerValue.hours)
                                set(Calendar.MINUTE, pickerValue.minutes)
                            },
                            isEnabled = true,
                            vibration = isVibrationEnabled,
                            isSnoozeEnabled = isSnoozeEnabled,
                            deleteAfterGoesOff = deleteAfterGoesOff,
                            title = title,
                            subTitle = DateUtils.alarmTimeTo24HourFormat(
                                date = alarm.date
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
                contentDescription = stringResource(id = R.string.bottom_sheet_icon_save_content_description)
            )
        }
    }
}

@Composable
private fun BottomSheetTitle(
    modifier: Modifier = Modifier,
    title: String?,
    bottomText: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxHeight()
    ) {
        Text(
            text = if (!title.isNullOrBlank()) {
                title.toString()
            } else {
                stringResource(id = R.string.bottom_sheet_title)
            }
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = bottomText,
            style = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.secondary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomSheetTopBarPreview() {
    WhakaaraTheme {
        BottomSheetTopBar(
            coroutineScope = rememberCoroutineScope(),
            sheetState = BottomSheetState(),
            alarm = Alarm(
                date = Calendar.getInstance(),
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
}

@Preview(showBackground = true)
@Composable
fun BottomSheetTitlePreview() {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min)
    ) {
        BottomSheetTitle(
            title = "title",
            bottomText = "Off"
        )
    }
}
