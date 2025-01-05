package com.whakaara.feature.alarm.di

import android.app.Notification.CATEGORY_ALARM
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.whakaara.core.constants.NotificationUtilsConstants.CHANNEL_ID
import com.whakaara.feature.alarm.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {
    @Provides
    @Named("alarm")
    fun provideNotificationBuilder(
        @ApplicationContext
        context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID).apply {
            color = Color.WHITE
            setSmallIcon(R.drawable.baseline_access_time_24)
            setAutoCancel(true)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setCategory(CATEGORY_ALARM)
            setSubText(context.getString(R.string.notification_sub_text))
            priority = NotificationCompat.PRIORITY_MAX
        }
    }

    @Provides
    @Named("upcoming")
    fun providesNotificationBuilderForUpcomingAlarm(
        @ApplicationContext
        context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID).apply {
            color = Color.WHITE
            setSmallIcon(R.drawable.outline_timer_24)
            setCategory(CATEGORY_ALARM)
            setAutoCancel(true)
            setContentTitle(context.getString(R.string.upcoming_alarm_notification_content_title))
            setSubText(context.getString(R.string.upcoming_alarm_notification_sub_text))
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setUsesChronometer(true)
            setChronometerCountDown(true)
        }
    }
}
