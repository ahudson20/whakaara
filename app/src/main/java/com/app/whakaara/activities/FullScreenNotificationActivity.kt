package com.app.whakaara.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.service.MediaPlayerService
import com.app.whakaara.ui.screens.AlarmFullScreen
import com.app.whakaara.ui.screens.TimerFullScreen
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.GeneralUtils.Companion.showToast
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOP_FULL_SCREEN_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullScreenNotificationActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var alarm: Alarm

    private val broadCastReceiverFinishActivity = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                STOP_FULL_SCREEN_ACTIVITY -> finishAndRemoveTask()
                else -> return
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notificationType = intent.getIntExtra(NOTIFICATION_TYPE, -1)
        if (notificationType == -1) {
            finish()
        }

        hideSystemBars()
        turnScreenOnAndKeyguardOff()
        LocalBroadcastManager.getInstance(this).registerReceiver(broadCastReceiverFinishActivity, IntentFilter(STOP_FULL_SCREEN_ACTIVITY))

        if (notificationType == NOTIFICATION_TYPE_ALARM) {
            alarm = GeneralUtils.convertStringToAlarmObject(string = intent.getStringExtra(INTENT_EXTRA_ALARM))
        }

        setContent {
            val preferencesState by viewModel.preferencesUiState.collectAsStateWithLifecycle()

            WhakaaraTheme {
                if (notificationType == NOTIFICATION_TYPE_ALARM) {
                    AlarmFullScreen(
                        alarm = alarm,
                        snooze = viewModel::snooze,
                        disable = viewModel::disable,
                        is24HourFormat = preferencesState.preferences.is24HourFormat
                    )
                } else {
                    TimerFullScreen(
                        resetTimer = viewModel::resetTimer,
                        is24HourFormat = preferencesState.preferences.is24HourFormat
                    )
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        viewModel.disable(alarm = alarm)
        this@FullScreenNotificationActivity.showToast(message = this@FullScreenNotificationActivity.getString(R.string.notification_action_cancelled, alarm.title))
        finishAndRemoveTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        applicationContext.stopService(Intent(this@FullScreenNotificationActivity, MediaPlayerService::class.java))
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCastReceiverFinishActivity)
    }

    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun Activity.turnScreenOnAndKeyguardOff() {
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        with(getSystemService(KEYGUARD_SERVICE) as KeyguardManager) {
            requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
        }
    }
}
