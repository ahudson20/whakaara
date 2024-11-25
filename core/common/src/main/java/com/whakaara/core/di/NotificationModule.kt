package com.whakaara.core.di

import android.app.AlarmManager
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import com.whakaara.core.CountDownTimerUtil
import com.whakaara.core.constants.NotificationUtilsConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {
    @Provides
    @Singleton
    fun provideNotificationChannel() =
        NotificationChannel(
            NotificationUtilsConstants.CHANNEL_ID,
            NotificationUtilsConstants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            setBypassDnd(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setShowBadge(true)
            setSound(null, null)
        }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext
        context: Context,
        channel: NotificationChannel
    ): NotificationManager {
        return (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(channel)
        }
    }

    @Provides
    @Singleton
    fun providesCountDownTimerUtil(): CountDownTimerUtil = CountDownTimerUtil()

    @Provides
    @Singleton
    fun provideAlarmManager(app: Application): AlarmManager = app.getSystemService(Service.ALARM_SERVICE) as AlarmManager
}
