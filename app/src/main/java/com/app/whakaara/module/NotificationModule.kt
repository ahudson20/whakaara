package com.app.whakaara.module

import android.app.Notification
import android.app.Notification.CATEGORY_ALARM
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import com.app.whakaara.utils.constants.NotificationUtilsConstants.CHANNEL_ID
import com.google.android.material.R
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
    fun provideNotificationChannel() = NotificationChannel(
        CHANNEL_ID,
        NotificationUtilsConstants.CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        enableLights(true)
        setBypassDnd(true)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        setShowBadge(true)
        setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
    }

    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext
        context: Context,
        channel: NotificationChannel
    ): NotificationManager {
        val manager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(channel)
        }
        return manager
    }

    @Provides
    @Singleton
    fun provideNotificationBuilder(
        @ApplicationContext
        context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_clock_black_24dp)
            .setColor(Color.WHITE)
            .setAutoCancel(true)
            .setCategory(CATEGORY_ALARM)
            .setSubText(context.getString(com.app.whakaara.R.string.notification_sub_text))
    }
}
