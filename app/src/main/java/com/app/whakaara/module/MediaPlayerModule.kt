package com.app.whakaara.module

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.provider.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class MediaPlayerModule {

    @Provides
    fun provideMediaPlayer(
        @ApplicationContext
        context: Context
    ): MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        setDataSource(
            context,
            Settings.System.DEFAULT_ALARM_ALERT_URI ?: Settings.System.DEFAULT_RINGTONE_URI
        )
        isLooping = true
    }
}
