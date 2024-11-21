package com.whakaara.core.di

import android.app.Application
import android.app.Service
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.VolumeShaper
import android.os.PowerManager
import android.os.Vibrator
import android.os.VibratorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaPlayerModule {
    @Provides
    fun provideMediaPlayer(): MediaPlayer =
        MediaPlayer().apply {
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
    fun providesVibrator(app: Application): Vibrator =
        (app.getSystemService(Service.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator

    @Provides
    fun providesPowerManager(
        @ApplicationContext
        context: Context
    ): PowerManager = (context.getSystemService(Context.POWER_SERVICE) as PowerManager)

    @Provides
    fun providesVolumeShaperConfiguration(): VolumeShaper.Configuration.Builder {
        val times = floatArrayOf(0f, 1f)
        val volumes = floatArrayOf(0f, 1f)
        return VolumeShaper.Configuration.Builder()
            .setCurve(times, volumes)
            .setInterpolatorType(VolumeShaper.Configuration.INTERPOLATOR_TYPE_LINEAR)
    }
}
