package com.app.whakaara.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.whakaara.utils.NotificationUtils.Companion.startMediaService
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_AUTO_SILENCE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_RECEIVER_EXCEPTION_TAG
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.PLAY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        try {
            context.startMediaService(
                autoSilenceTime = intent.getIntExtra(
                    INTENT_AUTO_SILENCE,
                    ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES
                ),
                action = PLAY,
                type = NOTIFICATION_TYPE_ALARM,
                alarmId = intent.action
            )
        } catch (exception: Exception) {
            Log.d(NOTIFICATION_RECEIVER_EXCEPTION_TAG, exception.printStackTrace().toString())
        }
    }
}
