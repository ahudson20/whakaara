package com.app.whakaara.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.app.whakaara.R
import com.app.whakaara.data.Alarm
import com.app.whakaara.ui.clock.TextClock

@Composable
fun NotificationFullScreen(
    alarm: Alarm,
    snooze: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
) {
    val activity = (LocalContext.current as? Activity)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextClock()

        if (alarm.isSnoozeEnabled) {
            Button(
                onClick = {
                    snooze(alarm)
                    activity?.finish()
                }
            ) {
                Text(text = stringResource(id = R.string.notification_action_button_snooze))
            }
        }
        Button(
            onClick = {
                disable(alarm)
                activity?.finish()
            }
        ) {
            Text(text = stringResource(id = R.string.notification_action_button_dismiss))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationFullScreenPreview() {
    NotificationFullScreen(
        alarm = Alarm(
            minute = 3,
            hour = 10,
            isEnabled = false,
            subTitle = "10:03 AM"
        ),
        snooze = {},
        disable = {}
    )
}