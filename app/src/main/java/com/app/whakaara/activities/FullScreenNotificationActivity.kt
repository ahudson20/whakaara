package com.app.whakaara.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.whakaara.R
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.logic.MainViewModel
import com.app.whakaara.service.MediaPlayerService
import com.app.whakaara.ui.screens.NotificationFullScreen
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.GeneralUtils.Companion.showToast
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FullScreenNotificationActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var alarm: Alarm

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemBars()
        turnScreenOnAndKeyguardOff()

        alarm = GeneralUtils.convertStringToAlarmObject(string = intent.getStringExtra(INTENT_EXTRA_ALARM))

        setContent {
            val pref by viewModel.preferencesUiState.collectAsStateWithLifecycle()
            WhakaaraTheme {
                NotificationFullScreen(
                    alarm = alarm,
                    snooze = viewModel::snooze,
                    disable = viewModel::disable,
                    is24HourFormat = pref.preferences.is24HourFormat
                )
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
