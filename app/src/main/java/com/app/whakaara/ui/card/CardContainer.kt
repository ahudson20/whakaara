package com.app.whakaara.ui.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space10
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.GeneralUtils.Companion.showToast
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardContainerSwipeToDismiss(
    modifier: Modifier = Modifier,
    alarms: List<Alarm>,
    is24HourFormat: Boolean,
    delete: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    enable: (alarm: Alarm) -> Unit,
    reset: (alarm: Alarm) -> Unit
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
                        DismissBackground(dismissState)
                    },
                    content = {
                        Card(
                            alarm = alarm,
                            is24HourFormat = is24HourFormat,
                            disable = disable,
                            enable = enable,
                            reset = reset
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
fun CardContainerSwipeToDismissPreview() {
    WhakaaraTheme {
        CardContainerSwipeToDismiss(
            alarms = listOf(
                Alarm(
                    date = Calendar.getInstance(),
                    subTitle = "12:13 AM"
                )
            ),
            is24HourFormat = true,
            delete = {},
            disable = {},
            enable = {}
        ) {}
    }
}
