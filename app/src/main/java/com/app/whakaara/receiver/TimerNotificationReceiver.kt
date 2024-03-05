package com.app.whakaara.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.whakaara.utils.NotificationUtils.Companion.startMediaService
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_TIMER
import com.app.whakaara.utils.constants.NotificationUtilsConstants.PLAY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimerNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            context.startMediaService(
                autoSilenceTime = ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES,
                action = PLAY,
                type = NOTIFICATION_TYPE_TIMER
            )
        } catch (exception: Exception) {
            Log.d(NotificationUtilsConstants.TIMER_RECEIVER_EXCEPTION_TAG, exception.printStackTrace().toString())
        }
    }
}
