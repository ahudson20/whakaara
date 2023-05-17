package com.app.whakaara.ui.card

import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.whakaara.data.Alarm
import com.app.whakaara.ui.bottomsheet.BottomSheetWrapper
import com.app.whakaara.utils.DateUtils.Companion.getInitialTimeToAlarm
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.launch

@Composable
fun Card(
    alarm: Alarm,
    cancel: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState()
    val valueSlider by remember(alarm.isEnabled) { mutableStateOf(alarm.isEnabled) }
    var timeToAlarm by remember {
        mutableStateOf(
            getInitialTimeToAlarm(
                isEnabled = valueSlider,
                hours = alarm.hour,
                minutes = alarm.minute
            )
        )
    }
    
    LaunchedEffect(valueSlider) {
        timeToAlarm = getInitialTimeToAlarm(
            isEnabled = valueSlider,
            hours = alarm.hour,
            minutes = alarm.minute
        )
    }

    SystemBroadcastReceiver(
        IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
        }
    ) { _, _ ->
        timeToAlarm = getInitialTimeToAlarm(
            isEnabled = valueSlider,
            hours = alarm.hour,
            minutes = alarm.minute
        )
    }

    ElevatedCard(
        shape = RoundedCornerShape(0.dp),
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
//                        color = Color.Gray
                    )
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                    text = alarm.subTitle,
                    style = TextStyle(
                        fontSize = 20.sp,
//                        color = if (valueSlider) {
//                            Color.Black
//                        } else {
//                            Color.Gray
//                        }
                    )
                )
                Text(
                    text = timeToAlarm,
                    style = TextStyle(
                        fontSize = 14.sp,
//                        color = Color.Gray
                    )
                )
            }

            Spacer(Modifier.weight(1f))

            Switch(
                modifier = Modifier.padding(end = 20.dp),
                checked = valueSlider,
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

