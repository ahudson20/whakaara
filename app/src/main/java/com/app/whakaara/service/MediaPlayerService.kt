package com.app.whakaara.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.PowerManager
import android.os.Process.THREAD_PRIORITY_URGENT_AUDIO
import android.os.VibrationAttributes
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.whakaara.R
import com.app.whakaara.activities.FullScreenNotificationActivity
import com.app.whakaara.data.alarm.Alarm
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.preferences.Preferences
import com.app.whakaara.data.preferences.PreferencesRepository
import com.app.whakaara.data.preferences.VibrationPattern
import com.app.whakaara.data.preferences.VibrationPattern.Companion.REPEAT
import com.app.whakaara.module.IoDispatcher
import com.app.whakaara.receiver.MediaServiceReceiver
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.PendingIntentUtils
import com.app.whakaara.utils.constants.GeneralConstants
import com.app.whakaara.utils.constants.NotificationUtilsConstants.FOREGROUND_SERVICE_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_ALARM_ID
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ACTION_ARBITRARY
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_EXTRA_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_REQUEST_CODE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.MEDIA_SERVICE_EXCEPTION_TAG
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM
import com.app.whakaara.utils.constants.NotificationUtilsConstants.NOTIFICATION_TYPE_TIMER
import com.app.whakaara.utils.constants.NotificationUtilsConstants.PLAY
import com.app.whakaara.utils.constants.NotificationUtilsConstants.SERVICE_ACTION
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOP
import com.app.whakaara.utils.constants.NotificationUtilsConstants.STOP_FULL_SCREEN_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.util.TimerTask
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class MediaPlayerService : LifecycleService(), MediaPlayer.OnPreparedListener {

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var vibrator: Vibrator

    @Inject
    @Named("timer")
    lateinit var timerNotificationBuilder: NotificationCompat.Builder

    @Inject
    @Named("alarm")
    lateinit var alarmNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var powerManager: PowerManager

    @Inject
    @IoDispatcher
    lateinit var iODispatcher: CoroutineDispatcher

    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = Runnable {
        stop()
    }

    private lateinit var vibrationTask: TimerTask
    private lateinit var wakeLock: PowerManager.WakeLock

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
        val preferences: Preferences = runBlocking {
            preferencesRepository.getPreferences()
        }

        if (data.getInt(NOTIFICATION_TYPE) == NOTIFICATION_TYPE_ALARM) {
            val alarm = runBlocking {
                alarmRepository.getAlarmById(
                    id = UUID.fromString(data.getString(INTENT_ALARM_ID))
                )
            }

            val today = LocalDate.now().getDayOfWeek().value - 1
            if (alarm.daysOfWeek.isNotEmpty() && !alarm.daysOfWeek.contains(today)) {
                this.stopSelf()
            }

            if (!alarm.repeatDaily || alarm.daysOfWeek.isEmpty()) {
                if (alarm.deleteAfterGoesOff) {
                    deleteAlarmById(alarmId = alarm.alarmId)
                } else {
                    setIsEnabledToFalse(alarmId = alarm.alarmId)
                }
            }

            startForeground(
                FOREGROUND_SERVICE_ID,
                createAlarmNotification(alarm = alarm),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
            )

            if (preferences.isVibrateEnabled) vibrate(vibrationPattern = preferences.vibrationPattern)
        } else {
            startForeground(
                FOREGROUND_SERVICE_ID,
                createTimerNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
            )

            if (preferences.isVibrationTimerEnabled) vibrate(vibrationPattern = preferences.timerVibrationPattern)
        }

        if (!powerManager.isInteractive) {
            wakeLock = powerManager.run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, GeneralConstants.WAKE_LOCK_TAG).apply {
                    acquire(TimeUnit.MINUTES.toMillis(preferences.autoSilenceTime.value.toLong()))
                }
            }
        }

        mediaPlayer.apply {
            setDataSource(
                applicationContext,
                if (preferences.alarmSoundPath.isNotEmpty()) {
                    Uri.parse(preferences.alarmSoundPath)
                } else {
                    Settings.System.DEFAULT_ALARM_ALERT_URI
                }
            )
            setOnPreparedListener(this@MediaPlayerService)
            prepareAsync()
        }

        // set timeout on service
        handler.postDelayed(runnable, TimeUnit.MINUTES.toMillis(preferences.autoSilenceTime.value.toLong()))
    }

    private fun deleteAlarmById(alarmId: UUID) {
        lifecycleScope.launch(iODispatcher) {
            alarmRepository.deleteAlarmById(id = alarmId)
        }
    }

    private fun setIsEnabledToFalse(alarmId: UUID) {
        lifecycleScope.launch(iODispatcher) {
            alarmRepository.isEnabled(id = alarmId, isEnabled = false)
        }
    }

    private fun vibrate(
        vibrationPattern: VibrationPattern
    ) {
        vibrationTask = timerTask {
            val attributes = VibrationAttributes.Builder().apply {
                setUsage(VibrationAttributes.USAGE_ALARM)
            }.build()

            val vibrationEffect = VibrationPattern.createWaveForm(
                selection = vibrationPattern,
                repeat = REPEAT
            )
            vibrator.vibrate(vibrationEffect, attributes)
        }
        vibrationTask.run()
    }

    private fun stop() {
        // release wakelock
        if (::wakeLock.isInitialized) wakeLock.release()

        // Remove this service from foreground state, clear notification
        stopForeground(STOP_FOREGROUND_REMOVE)

        // cancel vibration if still running
        if (::vibrationTask.isInitialized) vibrationTask.cancel()
        vibrator.cancel()

        // cancel countdown timer
        handler.removeCallbacks(runnable)

        // stop ringtone if running, release resources associated
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }

            mediaPlayer.apply {
                reset()
                release()
            }
        } catch (exception: IllegalStateException) {
            Log.e(MEDIA_SERVICE_EXCEPTION_TAG, "MediaPlayer was not initialized.. Cannot stop it...")
        }

        // cancel fullScreenIntent
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(STOP_FULL_SCREEN_ACTIVITY))
    }

    private fun createAlarmNotification(
        alarm: Alarm
    ): Notification {
        val fullScreenIntent = Intent().apply {
            setClass(applicationContext, FullScreenNotificationActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(NOTIFICATION_TYPE, NOTIFICATION_TYPE_ALARM)
            putExtra(INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))

            /**
             * Unsure why I need to set an action here.
             * If I don't, I can't get the extras in the activity this intent starts.
             * https://stackoverflow.com/questions/15343840/intent-extras-missing-when-activity-started#:~:text=We%20stumbled%20upon,might%20fix%20it.
             * */
            action = INTENT_EXTRA_ACTION_ARBITRARY
        }
        val fullScreenPendingIntent = PendingIntentUtils.getActivity(
            applicationContext,
            INTENT_REQUEST_CODE,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopServiceIntent = Intent(applicationContext, MediaServiceReceiver::class.java).apply {
            putExtra(INTENT_ALARM_ID, alarm.alarmId.toString())
            putExtra(NOTIFICATION_TYPE, NOTIFICATION_TYPE_ALARM)
        }
        val stopServicePendingIntent = PendingIntentUtils.getBroadcast(
            context = applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = stopServiceIntent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )

        return alarmNotificationBuilder.apply {
            setContentTitle(alarm.title)
            setContentText(alarm.subTitle)
            setFullScreenIntent(fullScreenPendingIntent, true)
            setWhen(alarm.date.timeInMillis)
            setDeleteIntent(stopServicePendingIntent)
        }.build()
    }

    private fun createTimerNotification(): Notification {
        val fullScreenIntent = Intent().apply {
            setClass(applicationContext, FullScreenNotificationActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(NOTIFICATION_TYPE, NOTIFICATION_TYPE_TIMER)

            /**
             * Unsure why I need to set an action here.
             * If I don't, I can't get the extras in the activity this intent starts.
             * https://stackoverflow.com/questions/15343840/intent-extras-missing-when-activity-started#:~:text=We%20stumbled%20upon,might%20fix%20it.
             * */
            action = INTENT_EXTRA_ACTION_ARBITRARY
        }
        val fullScreenPendingIntent = PendingIntentUtils.getActivity(
            applicationContext,
            INTENT_REQUEST_CODE,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val intent = Intent(applicationContext, MediaServiceReceiver::class.java).apply {
            putExtra(NOTIFICATION_TYPE, NOTIFICATION_TYPE_ALARM)
        }
        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = applicationContext,
            id = INTENT_REQUEST_CODE,
            intent = intent,
            flag = PendingIntent.FLAG_UPDATE_CURRENT
        )
        return timerNotificationBuilder.apply {
            addAction(R.drawable.baseline_cancel_24, applicationContext.getString(R.string.timer_notification_action_label), pendingIntent)
            setFullScreenIntent(fullScreenPendingIntent, true)
            setDeleteIntent(pendingIntent)
            setContentText(applicationContext.getString(R.string.timer_notification_content_text))
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
        val bundle = Bundle().apply {
            putInt(SERVICE_ACTION, intent?.getIntExtra(SERVICE_ACTION, STOP) ?: STOP)
            putInt(NOTIFICATION_TYPE, intent?.getIntExtra(NOTIFICATION_TYPE, NOTIFICATION_TYPE_ALARM) ?: NOTIFICATION_TYPE_ALARM)
            putString(INTENT_ALARM_ID, intent?.getStringExtra(INTENT_ALARM_ID))
        }

        serviceHandler?.obtainMessage()?.also { msg ->
            msg.arg1 = startId
            msg.what = action
            msg.data = bundle
            serviceHandler?.sendMessage(msg)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer.start()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }
}
