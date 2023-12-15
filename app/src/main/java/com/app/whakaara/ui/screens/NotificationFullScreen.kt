package com.app.whakaara.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.ui.clock.TextClock
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.GeneralUtils.Companion.showToast
import java.util.Calendar

@Composable
fun NotificationFullScreen(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    snooze: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    is24HourFormat: Boolean
) {
    val context = LocalContext.current
    val activity = (context as? Activity)
    Scaffold { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row {
                TextClock(
                    is24HourFormat = is24HourFormat
                )
            }

            Row {
                if (alarm.isSnoozeEnabled) {
                    Button(
                        modifier = Modifier.padding(spaceMedium),
                        onClick = {
                            snooze(alarm)
                            context.showToast(
                                message = context.getString(R.string.notification_action_snoozed, alarm.title)
                            )
                            activity?.finish()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.notification_action_button_snooze))
                    }
                }
                Button(
                    modifier = Modifier.padding(spaceMedium),
                    onClick = {
                        disable(alarm)
                        context.showToast(message = context.getString(R.string.notification_action_cancelled, alarm.title))
                        activity?.finish()
                    }
                ) {
                    Text(text = stringResource(id = R.string.notification_action_button_dismiss))
                }
            }
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun NotificationFullScreenPreview() {
    WhakaaraTheme {
        NotificationFullScreen(
            alarm = Alarm(
                date = Calendar.getInstance(),
                isEnabled = false,
                subTitle = "10:03 AM"
            ),
            snooze = {},
            disable = {},
            is24HourFormat = true
        )
    }
}
