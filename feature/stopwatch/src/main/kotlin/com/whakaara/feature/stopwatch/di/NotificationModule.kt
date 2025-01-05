package com.whakaara.feature.stopwatch.di

import android.app.Notification.CATEGORY_STOPWATCH
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.whakaara.core.constants.NotificationUtilsConstants.CHANNEL_ID
import com.whakaara.feature.stopwatch.R
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
    @Named("stopwatch")
    fun providesNotificationBuilderForStopwatch(
        @ApplicationContext
        context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID).apply {
            color = Color.WHITE
            setSmallIcon(R.drawable.outline_timer_24)
            setCategory(CATEGORY_STOPWATCH)
            setAutoCancel(false)
            setOngoing(true)
            setSubText(context.getString(R.string.stopwatch_notification_sub_text))
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
    }
}
