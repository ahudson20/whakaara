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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.ui.clock.TextClock
import java.util.Calendar

@Composable
fun NotificationFullScreen(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    snooze: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit
) {
    val activity = (LocalContext.current as? Activity)
    Scaffold { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row {
                TextClock()
            }

            Row {
                if (alarm.isSnoozeEnabled) {
                    Button(
                        modifier = modifier.padding(16.dp),
                        onClick = {
                            snooze(alarm)
                            activity?.finish()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.notification_action_button_snooze))
                    }
                }
                Button(
                    modifier = modifier.padding(16.dp),
                    onClick = {
                        disable(alarm)
                        activity?.finish()
                    }
                ) {
                    Text(text = stringResource(id = R.string.notification_action_button_dismiss))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationFullScreenPreview() {
    NotificationFullScreen(
        alarm = Alarm(
            date = Calendar.getInstance(),
            isEnabled = false,
            subTitle = "10:03 AM"
        ),
        snooze = {},
        disable = {}
    )
}
