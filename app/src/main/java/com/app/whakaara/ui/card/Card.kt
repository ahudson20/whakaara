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
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.ui.bottomsheet.BottomSheetWrapper
import com.app.whakaara.utils.DateUtils
import com.app.whakaara.utils.DateUtils.Companion.getInitialTimeToAlarm
import com.app.whakaara.utils.GeneralUtils.Companion.showToast
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun Card(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState()
    val valueSlider by remember(alarm.isEnabled) { mutableStateOf(alarm.isEnabled) }
    var timeToAlarm by remember {
        mutableStateOf(
            getInitialTimeToAlarm(
                isEnabled = valueSlider,
                time = alarm.date
            )
        )
    }
    val alpha = if (valueSlider) 1f else ContentAlpha.disabled

    LaunchedEffect(key1 = alarm.date, key2 = valueSlider) {
        timeToAlarm = getInitialTimeToAlarm(
            isEnabled = valueSlider,
            time = alarm.date
        )
    }

    SystemBroadcastReceiver(
        IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
        }
    ) { _, _ ->
        timeToAlarm = getInitialTimeToAlarm(
            isEnabled = valueSlider,
            time = alarm.date
        )
    }

    ElevatedCard(
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                scope.launch { sheetState.expand() }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = alarm.title,
                    style = TextStyle(
                        fontSize = 14.sp
                    )
                )
                Text(
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp)
                        .alpha(alpha = alpha),
                    text = alarm.subTitle,
                    style = TextStyle(
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = timeToAlarm,
                    style = TextStyle(
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(modifier.weight(1f))

            Switch(
                modifier = modifier
                    .padding(end = 20.dp)
                    .testTag("alarm switch"),
                checked = valueSlider,
                onCheckedChange = {
                    if (!it) {
                        disable(alarm)
                        context.showToast(message = context.getString(R.string.notification_action_cancelled, alarm.title))
                    } else {
                        enable(alarm)
                        context.showToast(
                            message = DateUtils.convertSecondsToHMm(
                                seconds = TimeUnit.MILLISECONDS.toSeconds(
                                    DateUtils.getDifferenceFromCurrentTimeInMillis(
                                        time = alarm.date
                                    )
                                )
                            )
                        )
                    }
                }
            )
        }
    }

    BottomSheetWrapper(
        alarm = alarm,
        timeToAlarm = timeToAlarm,
        state = sheetState,
        reset = reset
    )
}

@Preview
@Composable
fun CardPreview() {
    Card(
        alarm = Alarm(
            date = Calendar.getInstance(),
            subTitle = "12:13 AM"
        ),
        disable = {},
        enable = {},
        reset = {}
    )
}
