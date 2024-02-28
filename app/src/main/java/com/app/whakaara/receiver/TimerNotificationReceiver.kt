package com.app.whakaara.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.whakaara.R
import com.app.whakaara.utils.NotificationUtils.Companion.startMediaService
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_TIMER_NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val notificationId = System.currentTimeMillis().toInt()
            val startReceiverIntent = Intent(context, MediaServiceReceiver::class.java).apply {
                putExtra(INTENT_TIMER_NOTIFICATION_ID, notificationId)
            }
            val pendingIntent = PendingIntentUtils.getBroadcast(
                context = context,
                id = INTENT_REQUEST_CODE,
                intent = startReceiverIntent,
                flag = INTENT_REQUEST_CODE
            )

            val notification = notificationBuilder.apply {
                setAutoCancel(false)
                setContentTitle(context.getString(R.string.timer_notification_content_title))
                setContentIntent(PendingIntent.getActivity(context, INTENT_REQUEST_CODE, Intent(), PendingIntent.FLAG_IMMUTABLE))
                addAction(R.drawable.baseline_cancel_24, context.getString(R.string.timer_notification_action_label), pendingIntent)
            }.build()

            notificationManager.notify(notificationId, notification)

            context.startMediaService(autoSilenceTime = ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES)
        } catch (exception: Exception) {
            Log.d(NotificationUtilsConstants.TIMER_RECEIVER_EXCEPTION_TAG, exception.printStackTrace().toString())
        }
    }
}
