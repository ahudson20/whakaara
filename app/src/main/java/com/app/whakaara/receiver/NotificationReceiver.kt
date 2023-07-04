package com.app.whakaara.receiver

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.whakaara.activities.FullScreenNotificationActivity
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ACTION_ARBITRARY
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repo: AlarmRepository

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onReceive(context: Context, intent: Intent) {
        try {
            val alarm = GeneralUtils.convertStringToAlarmObject(string = intent.getStringExtra(INTENT_EXTRA_ALARM))
            if (alarm.deleteAfterGoesOff) {
                deleteAlarmById(alarmId = alarm.alarmId)
            } else {
                setIsEnabledToFalse(alarmId = alarm.alarmId)
            }

            val notification = createNotification(
                context = context,
                alarm = alarm
            )

            enableVibrationForNotification(
                alarm = alarm
            )

            displayNotification(
                uniqueID = System.currentTimeMillis().toInt(),
                notification = notification
            )

            startAlarmSound()
        } catch (exception: Exception) {
            Log.d("Receiver exception", exception.printStackTrace().toString())
        }
    }

    private fun startAlarmSound() {
        mediaPlayer.apply {
            reset()
            prepare()
            start()
        }
    }

    private fun createNotification(
        context: Context,
        alarm: Alarm
    ): Notification {
        val fullScreenIntent = Intent().apply {
            setClass(context, FullScreenNotificationActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))

            /**
             * Unsure why I need to set an action here.
             * If I don't, I can't get the extras in the activity this intent starts.
             * https://stackoverflow.com/questions/15343840/intent-extras-missing-when-activity-started#:~:text=We%20stumbled%20upon,might%20fix%20it.
             * */
            action = INTENT_EXTRA_ACTION_ARBITRARY
        }
        val fullScreenPendingIntent = PendingIntentUtils.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = notificationBuilder.apply {
            // TODO::
            // setTimeoutAfter
            setContentTitle(alarm.title)
            setContentText(alarm.subTitle)
            setFullScreenIntent(fullScreenPendingIntent, true)
            setWhen(
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, alarm.hour)
                    set(Calendar.MINUTE, alarm.minute)
                }.timeInMillis
            )
        }.build()
        return notification
    }

    private fun deleteAlarmById(alarmId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.deleteAlarmById(id = alarmId)
        }
    }

    private fun setIsEnabledToFalse(alarmId: UUID) {
        CoroutineScope(Dispatchers.IO).launch {
            repo.isEnabled(id = alarmId, isEnabled = false)
        }
    }

    private fun enableVibrationForNotification(
        alarm: Alarm
    ) {
        notificationManager.getNotificationChannel(NotificationUtilsConstants.CHANNEL_ID).apply { enableVibration(alarm.vibration) }
    }

    private fun displayNotification(
        uniqueID: Int,
        notification: Notification
    ) {
        notificationManager.notify(uniqueID, notification)
    }
}
