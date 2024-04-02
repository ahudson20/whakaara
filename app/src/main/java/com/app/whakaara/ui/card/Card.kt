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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.ui.bottomsheet.details.BottomSheetDetailsContent
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space100
import com.app.whakaara.ui.theme.Spacings.space20
import com.app.whakaara.ui.theme.Spacings.space28
import com.app.whakaara.ui.theme.Spacings.space80
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.DateUtils.Companion.getInitialTimeToAlarm
import com.app.whakaara.utils.DateUtils.Companion.getTimeUntilAlarmFormatted
import com.app.whakaara.utils.GeneralUtils.Companion.showToast
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.rememberBottomSheetState
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun Card(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    is24HourFormat: Boolean,
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
                time = alarm.date,
                context = context
            )
        )
    }
    val alpha = if (valueSlider) 1f else 0.60f

    LaunchedEffect(key1 = alarm.date, key2 = valueSlider) {
        timeToAlarm = getInitialTimeToAlarm(
            isEnabled = valueSlider,
            time = alarm.date,
            context = context
        )
    }

    SystemBroadcastReceiver(
        IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
        }
    ) { _, _ ->
        timeToAlarm = getInitialTimeToAlarm(
            isEnabled = valueSlider,
            time = alarm.date,
            context = context
        )
    }

    ElevatedCard(
        shape = RoundedCornerShape(space100),
        modifier = modifier
            .fillMaxWidth()
            .height(space80)
            .clip(RoundedCornerShape(space100))
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
                    .padding(start = space28, end = space28),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.alpha(alpha = alpha),
                    text = alarm.subTitle.filterNot { it.isWhitespace() },
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    modifier = Modifier.alpha(alpha = alpha),
                    text = timeToAlarm,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.weight(1f))

            Switch(
                modifier = Modifier
                    .padding(end = space20)
                    .testTag("alarm switch"),
                checked = valueSlider,
                onCheckedChange = {
                    if (!it) {
                        disable(alarm)
                        context.showToast(message = context.getString(R.string.notification_action_cancelled, alarm.title))
                    } else {
                        enable(alarm)
                        context.showToast(
                            message = context.getTimeUntilAlarmFormatted(date = alarm.date)
                        )
                    }
                }
            )
        }
    }

    BottomSheet(
        backgroundColor = MaterialTheme.colorScheme.surface,
        state = sheetState,
        skipPeeked = true
    ) {
        BottomSheetDetailsContent(
            alarm = alarm,
            timeToAlarm = timeToAlarm,
            is24HourFormat = is24HourFormat,
            sheetState = sheetState,
            reset = reset
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun CardPreview() {
    WhakaaraTheme {
        Card(
            alarm = Alarm(
                date = Calendar.getInstance(),
                subTitle = "12:13 AM"
            ),
            is24HourFormat = true,
            disable = {},
            enable = {}
        ) {}
    }
}
