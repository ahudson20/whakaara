package com.app.whakaara.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.app.whakaara.ui.clock.TextClock
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utility.GeneralUtils.Companion.showToast

@Composable
fun TimerFullScreen(
    resetTimer: () -> Unit,
    is24HourFormat: Boolean,
) {
    val context = LocalContext.current
    val activity = (context as? Activity)
    Scaffold { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            Text(
                text = stringResource(id = R.string.timer_notification_content_title),
                style = MaterialTheme.typography.titleLarge,
            )

            Row {
                TextClock(
                    is24HourFormat = is24HourFormat,
                )
            }

            Row {
                Button(
                    modifier = Modifier.padding(spaceMedium),
                    onClick = {
                        resetTimer()
                        context.showToast(message = context.getString(R.string.timer_full_screen_cancelled))
                        activity?.finish()
                    },
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
fun TimerFullScreenPreview() {
    WhakaaraTheme {
        TimerFullScreen(
            resetTimer = {},
            is24HourFormat = true,
        )
    }
}
