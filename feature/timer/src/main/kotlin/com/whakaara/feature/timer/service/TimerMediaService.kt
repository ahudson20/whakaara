package com.whakaara.feature.timer.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.media.VolumeShaper
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
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.whakaara.core.GeneralUtils.Companion.parseOrDefault
import com.whakaara.core.LogUtils.logE
import com.whakaara.core.PendingIntentUtils
import com.whakaara.core.constants.GeneralConstants
import com.whakaara.core.constants.GeneralConstants.FULL_SCREEN_NOTIFICATION_ACTIVITY
import com.whakaara.core.constants.NotificationUtilsConstants
import com.whakaara.core.di.IoDispatcher
import com.whakaara.core.di.MainDispatcher
import com.whakaara.data.preferences.PreferencesRepository
import com.whakaara.feature.timer.R
import com.whakaara.feature.timer.reciever.TimerMediaServiceReceiver
import com.whakaara.model.preferences.GradualSoundDuration
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.VibrationPattern
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.TimerTask
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class TimerMediaService : LifecycleService(), MediaPlayer.OnPreparedListener {

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    @Inject
    lateinit var vibrator: Vibrator

    @Inject
    lateinit var powerManager: PowerManager

    @Inject
    lateinit var volumeShaperConfiguration: VolumeShaper.Configuration.Builder

    @Inject
    @Named("timer")
    lateinit var timerNotificationBuilder: NotificationCompat.Builder

    @Inject
    @IoDispatcher
    lateinit var iODispatcher: CoroutineDispatcher

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

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
            val preferences: Preferences = preferencesRepository.getPreferences()

            withContext(mainDispatcher) {
                setupMediaPlayer(
                    soundPath = preferences.timerSoundPath,
                    duration = preferences.timerGradualSoundDuration.inMillis()
                )

                startForeground(
                    NotificationUtilsConstants.FOREGROUND_SERVICE_ID,
                    createTimerNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
                )

                if (preferences.isVibrationTimerEnabled) vibrate(vibrationPattern = preferences.timerVibrationPattern)

                if (!powerManager.isInteractive) {
                    wakeLock = powerManager.run {
                        newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, GeneralConstants.WAKE_LOCK_TAG).apply {
                            acquire(TimeUnit.MINUTES.toMillis(preferences.autoSilenceTime.value.toLong()))
                        }
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
                parseOrDefault(soundPath)
            )
            setOnPreparedListener(this@TimerMediaService)
            prepareAsync()
        }
    }

    private fun createTimerNotification(): Notification {
        val fullScreenIntent = Intent().apply {
            setClassName(
                applicationContext.packageName,
                FULL_SCREEN_NOTIFICATION_ACTIVITY
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(NotificationUtilsConstants.NOTIFICATION_TYPE, NotificationUtilsConstants.NOTIFICATION_TYPE_TIMER)

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

        val intent = Intent(applicationContext, TimerMediaServiceReceiver::class.java).apply {
            putExtra(NotificationUtilsConstants.NOTIFICATION_TYPE, NotificationUtilsConstants.NOTIFICATION_TYPE_ALARM)
        }
        val pendingIntent = PendingIntentUtils.getBroadcast(
            context = applicationContext,
            id = NotificationUtilsConstants.INTENT_REQUEST_CODE,
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
            logE(message = "MediaPlayer was not initialized.. Cannot stop it...", throwable = exception)
        }

        // cancel fullScreenIntent
        sendBroadcast(Intent(NotificationUtilsConstants.STOP_FULL_SCREEN_ACTIVITY))
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

    override fun onCreate() {
        super.onCreate()

        HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_URGENT_AUDIO).apply {
            start()

            serviceLooper = looper
            serviceHandler = ServiceHandler(looper)
        }
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
