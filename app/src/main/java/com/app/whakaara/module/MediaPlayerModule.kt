package com.app.whakaara.module

import android.app.Application
import android.app.Service
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Vibrator
import android.os.VibratorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaPlayerModule {

    @Provides
    fun provideMediaPlayer(): MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        isLooping = true
    }

    @Provides
    @Singleton
    fun providesVibrator(
        app: Application
    ): Vibrator = (app.getSystemService(Service.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
}
