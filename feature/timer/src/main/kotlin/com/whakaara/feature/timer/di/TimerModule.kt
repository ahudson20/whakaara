package com.whakaara.feature.timer.di

import android.app.Notification
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.whakaara.core.constants.NotificationUtilsConstants
import com.whakaara.feature.timer.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class TimerModule {
    @Provides
    @Named("timer")
    fun provideNotificationBuilderForTimer(
        @ApplicationContext
        context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NotificationUtilsConstants.CHANNEL_ID).apply {
            color = Color.WHITE
            setSmallIcon(R.drawable.baseline_access_time_24)
            setCategory(Notification.CATEGORY_ALARM)
            setAutoCancel(false)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContentTitle(context.getString(R.string.timer_notification_content_title))
        }
    }
}
