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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoDelete
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.app.whakaara.R
import com.app.whakaara.ui.bottomsheet.details.BottomSheetDetailsContent
import com.app.whakaara.utility.GeneralUtils.Companion.showToast
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.rememberBottomSheetState
import com.whakaara.core.designsystem.theme.AlarmPreviewProvider
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.space10
import com.whakaara.core.designsystem.theme.Spacings.space20
import com.whakaara.core.designsystem.theme.Spacings.space28
import com.whakaara.core.designsystem.theme.Spacings.space80
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.preferences.TimeFormat
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun Card(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    timeFormat: TimeFormat,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit,
    getInitialTimeToAlarm: (isEnabled: Boolean, time: Calendar) -> String,
    getTimeUntilAlarmFormatted: (date: Calendar) -> String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberBottomSheetState()
    val valueSlider by remember(alarm.isEnabled) { mutableStateOf(alarm.isEnabled) }
    var timeToAlarm by remember { mutableStateOf(getInitialTimeToAlarm(valueSlider, alarm.date)) }
    val alpha = if (valueSlider) 1f else 0.60f

    LaunchedEffect(key1 = alarm.date, key2 = valueSlider) {
        timeToAlarm = getInitialTimeToAlarm(valueSlider, alarm.date)
    }

    SystemBroadcastReceiver(
        IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
        }
    ) { _, _ ->
        timeToAlarm = getInitialTimeToAlarm(valueSlider, alarm.date)
    }

    ElevatedCard(
        shape = com.whakaara.core.designsystem.theme.Shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth()
            .height(space80)
            .clip(com.whakaara.core.designsystem.theme.Shapes.extraLarge)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.alpha(alpha = alpha),
                        text = alarm.subTitle.filterNot { it.isWhitespace() },
                        style = MaterialTheme.typography.headlineSmall
                    )

                    if (alarm.repeatDaily) {
                        Icon(
                            modifier = Modifier.padding(start = space10).size(spaceMedium),
                            imageVector = Icons.Outlined.Repeat,
                            contentDescription = stringResource(id = R.string.card_repeat_daily_icon_content_description)
                        )
                    } else if (alarm.deleteAfterGoesOff) {
                        Icon(
                            modifier = Modifier.padding(start = space10).size(spaceMedium),
                            imageVector = Icons.Outlined.AutoDelete,
                            contentDescription = stringResource(id = R.string.card_alarm_single_shot_content_description)
                        )
                    }
                }
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
                            message = getTimeUntilAlarmFormatted(alarm.date)
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
            timeFormat = timeFormat,
            sheetState = sheetState,
            reset = reset,
            getTimeUntilAlarmFormatted = getTimeUntilAlarmFormatted
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun CardPreview(
    @PreviewParameter(AlarmPreviewProvider::class) alarm: Alarm
) {
    WhakaaraTheme {
        Card(
            alarm = alarm,
            timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
            disable = {},
            enable = {},
            reset = {},
            getInitialTimeToAlarm = { _, _ -> "getInitialTimeToAlarm" }
        ) { "getTimeUntilAlarmFormatted" }
    }
}
