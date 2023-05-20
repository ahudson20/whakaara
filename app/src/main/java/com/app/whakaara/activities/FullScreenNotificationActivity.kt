package com.app.whakaara.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.app.whakaara.R
import com.app.whakaara.data.Alarm
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.ui.clock.TextClock
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullScreenNotificationActivity: ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var alarm: Alarm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()

        alarm = GeneralUtils.convertStringToAlarmObject(string = intent.getStringExtra(INTENT_EXTRA_ALARM))

        setContent {
            WhakaaraTheme {
                NotificationFullScreen()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        viewModel.disable(alarm = alarm)
        finishAndRemoveTask()
    }

    @Composable
    private fun NotificationFullScreen() {
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
                        viewModel.snooze(alarm = alarm)
                        finishAndRemoveTask()
                    }
                ) {
                    Text(text = stringResource(id = R.string.notification_action_button_snooze))
                }
            }
            Button(
                onClick = {
                    viewModel.disable(alarm = alarm)
                    finishAndRemoveTask()
                }
            ) {
                Text(text = stringResource(id = R.string.notification_action_button_dismiss))
            }
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}