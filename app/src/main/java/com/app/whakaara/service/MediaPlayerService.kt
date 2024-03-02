package com.app.whakaara.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Process.THREAD_PRIORITY_URGENT_AUDIO
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.whakaara.R
import com.app.whakaara.activities.FullScreenNotificationActivity
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.receiver.MediaServiceReceiver
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_NOTIFICATION_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_ALARM_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ACTION_ARBITRARY
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.MEDIA_SERVICE_EXCEPTION_TAG
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.PLAY
import com.app.whakaara.utils.constants.NotificationUtilsConstants.SERVICE_ACTION
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOP
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener {

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var repo: AlarmRepository

    @Inject
    @Named("timer")
    lateinit var timerNotificationBuilder: NotificationCompat.Builder

    @Inject
    @Named("alarm")
    lateinit var alarmNotificationBuilder: NotificationCompat.Builder

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private inner class ServiceHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                PLAY -> play(msg.data)
                STOP -> {
                    stopSelf(msg.arg1)
                }
            }
        }
    }

    private fun play(
        data: Bundle
    ) {
        if (data.getInt(NOTIFICATION_TYPE) == NOTIFICATION_TYPE_ALARM) {
            val alarm = runBlocking {
                repo.getAlarmById(
                    id = UUID.fromString(data.getString(INTENT_ALARM_ID))
                )
            }

            if (alarm.deleteAfterGoesOff) {
                deleteAlarmById(alarmId = alarm.alarmId)
            } else {
                setIsEnabledToFalse(alarmId = alarm.alarmId)
            }

            val notification = createAlarmNotification(alarm = alarm)
            notificationManager.notify(
                ALARM_NOTIFICATION_ID,
                notification
            )
        } else {
            notificationManager.notify(
                ALARM_NOTIFICATION_ID,
                createTimerNotification()
            )
        }

        mediaPlayer.apply {
            setOnPreparedListener(this@MediaPlayerService)
            prepareAsync()
        }
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

    private fun stop() {
        notificationManager.cancel(ALARM_NOTIFICATION_ID)

        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
        } catch (exception: IllegalStateException) {
            Log.e(MEDIA_SERVICE_EXCEPTION_TAG, "MediaPlayer was not initialized.. Cannot stop it...")
        }
        mediaPlayer.apply {
            reset()
            release()
        }
    }

    private fun createAlarmNotification(
        alarm: Alarm
    ): Notification {
        val intent = Intent().apply {
            setClass(applicationContext, FullScreenNotificationActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))

            /**
             * Unsure why I need to set an action here.
             * If I don't, I can't get the extras in the activity this intent starts.
             * https://stackoverflow.com/questions/15343840/intent-extras-missing-when-activity-started#:~:text=We%20stumbled%20upon,might%20fix%20it.
             * */
            action = INTENT_EXTRA_ACTION_ARBITRARY
        }
        val pendingIntent = PendingIntentUtils.getActivity(
            applicationContext,
            INTENT_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return alarmNotificationBuilder.apply {
            setContentTitle(alarm.title)
            setContentText(alarm.subTitle)
            setFullScreenIntent(pendingIntent, true)
            setWhen(alarm.date.timeInMillis)
        }.build()
    }

    private fun createTimerNotification(): Notification {
        val intent = Intent(applicationContext, MediaServiceReceiver::class.java)
        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = intent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )
        return timerNotificationBuilder.apply {
            addAction(R.drawable.baseline_cancel_24, applicationContext.getString(R.string.timer_notification_action_label), pendingIntent)
        }.build()
    }

    override fun onCreate() {
        super.onCreate()

        HandlerThread("ServiceStartArguments", THREAD_PRIORITY_URGENT_AUDIO).apply {
            start()

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action: Int = intent?.getIntExtra(SERVICE_ACTION, STOP) ?: STOP
        val notificationType: Int = intent?.getIntExtra(NOTIFICATION_TYPE, NOTIFICATION_TYPE_ALARM) ?: NOTIFICATION_TYPE_ALARM
        val alarmId: String? = intent?.getStringExtra(INTENT_ALARM_ID)
        val bundle = Bundle().apply {
            putInt(SERVICE_ACTION, action)
            putInt(NOTIFICATION_TYPE, notificationType)
            putString(INTENT_ALARM_ID, alarmId)
        }

        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            msg.what = action
            msg.data = bundle
            serviceHandler?.sendMessage(msg)
        }

        return START_STICKY
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer.start()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }
}
