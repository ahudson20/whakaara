package com.whakaara.feature.alarm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.whakaara.core.designsystem.theme.AlarmPreviewProvider
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.space10
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.feature.alarm.R
import com.whakaara.feature.alarm.utils.GeneralUtils.Companion.showToast
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.preferences.TimeFormat
import java.util.Calendar

@Composable
fun CardContainerSwipeToDismiss(
    modifier: Modifier = Modifier,
    alarms: List<Alarm>,
    timeFormat: TimeFormat,
    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit,
    getInitialTimeToAlarm: (isEnabled: Boolean, time: Calendar) -> String,
    getTimeUntilAlarmFormatted: (date: Calendar) -> String
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier
    ) {
        if (alarms.isEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = space10),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.alarm_screen_empty_list)
                    )
                }
            }
        } else {
            items(alarms, key = { it.alarmId }) { alarm ->
                val dismissState = rememberSwipeToDismissBoxState(
                    positionalThreshold = { distance: Float ->
                        distance * 0.7f
                    }
                )

                if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                    LaunchedEffect(Unit) {
                        context.showToast(context.getString(R.string.notification_action_deleted, alarm.title))
                        delete(alarm)
                    }
                }

                SwipeToDismissBox(
                    modifier = Modifier.padding(start = spaceMedium, end = spaceMedium, bottom = space10),
                    state = dismissState,
                    enableDismissFromStartToEnd = false,
                    backgroundContent = {
                        DismissBackground(dismissState = dismissState)
                    },
                    content = {
                        Card(
                            alarm = alarm,
                            timeFormat = timeFormat,
                            disable = disable,
                            enable = enable,
                            reset = reset,
                            getInitialTimeToAlarm = getInitialTimeToAlarm,
                            getTimeUntilAlarmFormatted = getTimeUntilAlarmFormatted
                        )
                    }
                )
            }
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun CardContainerSwipeToDismissPreview(
    @PreviewParameter(AlarmPreviewProvider::class) alarm: Alarm
) {
    WhakaaraTheme {
        CardContainerSwipeToDismiss(
            alarms = listOf(
                alarm
            ),
            timeFormat = TimeFormat.TWENTY_FOUR_HOURS,
            delete = {},
            disable = {},
            enable = {},
            reset = {},
            getInitialTimeToAlarm = { _, _ -> "getInitialTimeToAlarm" }
        ) { "getTimeUntilAlarmFormatted" }
    }
}
