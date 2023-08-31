package com.app.whakaara.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_SOUND_TIMEOUT_DEFAULT_MILLIS
import com.app.whakaara.utils.constants.NotificationUtilsConstants.ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES
import com.app.whakaara.utils.constants.NotificationUtilsConstants.INTENT_AUTO_SILENCE
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MediaPlayerService : Service(), MediaPlayer.OnPreparedListener {

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer.apply {
            setOnPreparedListener(this@MediaPlayerService)
            prepareAsync()
        }

        val autoSilenceTime = intent?.getIntExtra(INTENT_AUTO_SILENCE, ALARM_SOUND_TIMEOUT_DEFAULT_MINUTES)?.toLong()

        Handler(Looper.getMainLooper()).postDelayed({
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
        }, TimeUnit.MINUTES.toMillis(autoSilenceTime ?: ALARM_SOUND_TIMEOUT_DEFAULT_MILLIS))
        return START_STICKY
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}
