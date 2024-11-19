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
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.app.whakaara.R
import com.app.whakaara.ui.clock.TextClock
import com.app.whakaara.utility.GeneralUtils.Companion.showToast
import com.whakaara.core.designsystem.theme.AlarmPreviewProvider
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.preferences.TimeFormat

@Composable
fun AlarmFullScreen(
    modifier: Modifier = Modifier,
    alarm: Alarm,
    snooze: (alarm: Alarm) -> Unit,
    disable: (alarm: Alarm) -> Unit,
    timeFormat: TimeFormat
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
                    timeFormat = timeFormat
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
@com.whakaara.core.designsystem.theme.FontScalePreviews
private fun NotificationFullScreenPreview(
    @PreviewParameter(AlarmPreviewProvider::class) alarm: Alarm
) {
    WhakaaraTheme {
        AlarmFullScreen(
            alarm = alarm,
            snooze = {},
            disable = {},
            timeFormat = TimeFormat.TWENTY_FOUR_HOURS
        )
    }
}
