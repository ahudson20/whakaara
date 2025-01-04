package com.whakaara.feature.alarm.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.media.VolumeShaper
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.PowerManager
import android.os.Process
import android.os.VibrationAttributes
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.whakaara.core.PendingIntentUtils
import com.whakaara.core.constants.GeneralConstants
import com.whakaara.core.constants.NotificationUtilsConstants
import com.whakaara.core.di.IoDispatcher
import com.whakaara.core.di.MainDispatcher
import com.whakaara.data.alarm.AlarmRepository
import com.whakaara.data.preferences.PreferencesRepository
import com.whakaara.feature.alarm.receiver.AlarmMediaServiceReceiver
import com.whakaara.feature.alarm.utils.GeneralUtils
import com.whakaara.model.alarm.Alarm
import com.whakaara.model.preferences.GradualSoundDuration
import com.whakaara.model.preferences.VibrationPattern
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.TimerTask
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class AlarmMediaService : LifecycleService(), MediaPlayer.OnPreparedListener {

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var alarmRepository: AlarmRepository

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var vibrator: Vibrator

    @Inject
    @Named("alarm")
    lateinit var alarmNotificationBuilder: NotificationCompat.Builder

    @Inject
    lateinit var powerManager: PowerManager

    @Inject
    @IoDispatcher
    lateinit var iODispatcher: CoroutineDispatcher

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    lateinit var volumeShaperConfiguration: VolumeShaper.Configuration.Builder

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
                NotificationUtilsConstants.PLAY -> play(msg.data)
                NotificationUtilsConstants.STOP -> {
                    stopSelf(msg.arg1)
                }
            }
        }
    }

    private fun play(data: Bundle) {
        lifecycleScope.launch(iODispatcher) {
            val preferences = preferencesRepository.getPreferences()
            val alarm = alarmRepository.getAlarmById(
                id = UUID.fromString(data.getString(NotificationUtilsConstants.INTENT_ALARM_ID))
            )

            withContext(mainDispatcher) {
                val today = LocalDate.now().dayOfWeek.value - 1
                if (alarm.daysOfWeek.isNotEmpty() && !alarm.daysOfWeek.contains(today)) {
                    stopSelf()
                    return@withContext
                }

                if (!alarm.repeatDaily || alarm.daysOfWeek.isEmpty()) {
                    if (alarm.deleteAfterGoesOff) {
                        deleteAlarmById(alarm.alarmId)
                    } else {
                        setIsEnabledToFalse(alarm.alarmId)
                    }
                }

                setupMediaPlayer(
                    soundPath = preferences.alarmSoundPath,
                    duration = preferences.gradualSoundDuration.inMillis()
                )

                startForeground(
                    NotificationUtilsConstants.FOREGROUND_SERVICE_ID,
                    createAlarmNotification(alarm = alarm),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
                )

                if (preferences.isVibrateEnabled) {
                    vibrate(vibrationPattern = preferences.vibrationPattern)
                }

                if (!powerManager.isInteractive) {
                    wakeLock = powerManager.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK,
                        GeneralConstants.WAKE_LOCK_TAG
                    ).apply {
                        acquire(TimeUnit.MINUTES.toMillis(preferences.autoSilenceTime.value.toLong()))
                    }
                }

                // set timeout on service
                handler.postDelayed(runnable, TimeUnit.MINUTES.toMillis(preferences.autoSilenceTime.value.toLong()))
            }
        }
    }

    private fun setupMediaPlayer(soundPath: String, duration: Long) {
        if (duration > GradualSoundDuration.GRADUAL_INCREASE_DURATION_NEVER.seconds) {
            volumeShaperConfiguration.apply { setDuration(duration) }
        }

        mediaPlayer.apply {
            setDataSource(
                applicationContext,
                if (soundPath.isNotEmpty()) {
                    Uri.parse(soundPath)
                } else {
                    Settings.System.DEFAULT_ALARM_ALERT_URI
                }
            )
            setOnPreparedListener(this@AlarmMediaService)
            prepareAsync()
        }
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

    private fun vibrate(vibrationPattern: VibrationPattern) {
        vibrationTask = timerTask {
            val attributes = VibrationAttributes.Builder().apply {
                setUsage(VibrationAttributes.USAGE_ALARM)
            }.build()

            val vibrationEffect = VibrationPattern.createWaveForm(
                selection = vibrationPattern,
                repeat = VibrationPattern.REPEAT
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
            Log.e(NotificationUtilsConstants.MEDIA_SERVICE_EXCEPTION_TAG, "MediaPlayer was not initialized.. Cannot stop it...")
        }

        // cancel fullScreenIntent
        sendBroadcast(Intent(NotificationUtilsConstants.STOP_FULL_SCREEN_ACTIVITY))
    }

    private fun createAlarmNotification(alarm: Alarm): Notification {
        val fullScreenIntent = Intent().apply {
            setClassName(
                applicationContext.packageName,
                "com.app.whakaara.activities.FullScreenNotificationActivity"
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(NotificationUtilsConstants.NOTIFICATION_TYPE, NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM)
            putExtra(NotificationUtilsConstants.INTENT_EXTRA_ALARM, GeneralUtils.convertAlarmObjectToString(alarm))

            /**
             * Unsure why I need to set an action here.
             * If I don't, I can't get the extras in the activity this intent starts.
             * https://stackoverflow.com/questions/15343840/intent-extras-missing-when-activity-started#:~:text=We%20stumbled%20upon,might%20fix%20it.
             * */
            action = NotificationUtilsConstants.INTENT_EXTRA_ACTION_ARBITRARY
        }
        val fullScreenPendingIntent = PendingIntentUtils.getActivity(
            applicationContext,
            NotificationUtilsConstants.INTENT_REQUEST_CODE,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopServiceIntent = Intent(applicationContext, AlarmMediaServiceReceiver::class.java).apply {
            putExtra(NotificationUtilsConstants.INTENT_ALARM_ID, alarm.alarmId.toString())
            putExtra(NotificationUtilsConstants.NOTIFICATION_TYPE, NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM)
        }
        val stopServicePendingIntent = PendingIntentUtils.getBroadcast(
            context = applicationContext,
            id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
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

    override fun onCreate() {
        super.onCreate()

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_URGENT_AUDIO).apply {
            start()

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        val action: Int = intent?.getIntExtra(NotificationUtilsConstants.SERVICE_ACTION, NotificationUtilsConstants.STOP) ?: NotificationUtilsConstants.STOP
        val bundle =
            Bundle().apply {
                putInt(NotificationUtilsConstants.SERVICE_ACTION, intent?.getIntExtra(NotificationUtilsConstants.SERVICE_ACTION, NotificationUtilsConstants.STOP) ?: NotificationUtilsConstants.STOP)
                putInt(NotificationUtilsConstants.NOTIFICATION_TYPE, intent?.getIntExtra(NotificationUtilsConstants.NOTIFICATION_TYPE, NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM) ?: NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM)
                putString(NotificationUtilsConstants.INTENT_ALARM_ID, intent?.getStringExtra(NotificationUtilsConstants.INTENT_ALARM_ID))
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
        val volumeShaper = mediaPlayer.createVolumeShaper(volumeShaperConfiguration.build())
        mediaPlayer.start()
        volumeShaper.apply(VolumeShaper.Operation.PLAY)
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
