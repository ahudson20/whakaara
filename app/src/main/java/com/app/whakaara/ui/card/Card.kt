package com.app.whakaara.ui.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.whakaara.R
import com.app.whakaara.data.Alarm
import com.app.whakaara.ui.bottomsheet.BottomSheetWrapper
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.DateUtils.Companion.alarmTimeTo24HourFormat
import com.app.whakaara.utils.DateUtils.Companion.getInitialTimeToAlarm
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun Card(
    alarm: Alarm,
    cancel: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState()
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                scope.launch { sheetState.expand() }
            },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = alarm.title,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                    text = alarmTimeTo24HourFormat(alarm.hour, alarm.minute),
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = if (alarm.isEnabled) {
                            Color.Black
                        } else {
                            Color.Gray
                        }
                    )
                )
                Text(
                    text = getInitialTimeToAlarm(
                        isEnabled = alarm.isEnabled,
                        hours = alarm.hour,
                        minutes = alarm.minute
                    ),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                )
            }
            Spacer(Modifier.weight(1f))
            Switch(
                modifier = Modifier.padding(end = 20.dp),
                checked = alarm.isEnabled,
                onCheckedChange = {
                    if (!it) {
                        cancel(alarm)
                    } else {
                        enable(alarm)
                    }
                }
            )
        }
    }

    BottomSheetWrapper(
        state = sheetState,
        alarm = alarm,
        reset = reset
    )
}

@Preview
@Composable
fun CardPreview() {
    Card(
        alarm = Alarm(
            hour = 12,
            minute = 13,
            subTitle = "12:13 AM"
        ),
        cancel = {},
        enable = {},
        reset = {}
    )
}

