package com.app.whakaara.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.whakaara.service.MediaPlayerService
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaServiceReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        try {
            context.stopService(Intent(context, MediaPlayerService::class.java))
            notificationManager.cancel(ALARM_NOTIFICATION_ID)
        } catch (exception: Exception) {
            Log.d(NotificationUtilsConstants.MEDIA_SERVICE_RECEIVER_EXCEPTION_TAG, exception.printStackTrace().toString())
        }
    }
}
