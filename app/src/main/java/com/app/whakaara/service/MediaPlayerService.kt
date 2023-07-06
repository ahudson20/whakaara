package com.app.whakaara.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
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

        // TODO: timeout after time set in preferences.
//        Handler(Looper.getMainLooper()).postDelayed({
//            if (mediaPlayer.isPlaying) mediaPlayer.stop()
//        }, 3000)
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
