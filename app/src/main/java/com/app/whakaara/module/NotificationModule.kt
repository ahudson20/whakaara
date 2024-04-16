package com.app.whakaara.module

import android.app.AlarmManager
import android.app.Application
import android.app.Notification
import android.app.Notification.CATEGORY_ALARM
import android.app.Notification.CATEGORY_STOPWATCH
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.app.whakaara.R
import com.app.whakaara.data.datastore.PreferencesDataStore
import com.app.whakaara.logic.AlarmManagerWrapper
import com.app.whakaara.logic.CountDownTimerUtil
import com.app.whakaara.logic.StopwatchManagerWrapper
import com.app.whakaara.logic.TimerManagerWrapper
import com.app.whakaara.utils.constants.NotificationUtilsConstants
import com.app.whakaara.utils.constants.NotificationUtilsConstants.CHANNEL_ID
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Named
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
    @Named("timer")
    fun provideNotificationBuilderForTimer(
        @ApplicationContext
        context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID).apply {
            color = Color.WHITE
            setSmallIcon(R.drawable.baseline_access_time_24)
            setCategory(CATEGORY_ALARM)
            setAutoCancel(false)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setContentTitle(context.getString(R.string.timer_notification_content_title))
        }
    }

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
            setContentTitle(context.getString(R.string.shortcut_stopwatch_short_label))
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
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
            setContentTitle("Upcoming alarm")
            setSubText("Time to alarm")
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setUsesChronometer(true)
            setChronometerCountDown(true)
        }
    }

    @Provides
    @Singleton
    fun provideAlarmManager(app: Application): AlarmManager =
        app.getSystemService(Service.ALARM_SERVICE) as AlarmManager

    @Provides
    @Singleton
    fun providesAlarmManagerWrapper(
        app: Application,
        alarmManager: AlarmManager
    ): AlarmManagerWrapper = AlarmManagerWrapper(app, alarmManager)

    @Provides
    @Singleton
    fun providesTimerManagerWrapper(
        app: Application,
        alarmManager: AlarmManager,
        notificationManager: NotificationManager,
        @Named("timer")
        timerNotificationBuilder: NotificationCompat.Builder,
        countDownTimerUtil: CountDownTimerUtil,
        preferencesDataStore: PreferencesDataStore,
        coroutineScope: CoroutineScope
    ): TimerManagerWrapper = TimerManagerWrapper(app, alarmManager, notificationManager, timerNotificationBuilder, countDownTimerUtil, preferencesDataStore, coroutineScope)

    @Provides
    @Singleton
    fun providesStopwatchManagerWrapper(
        app: Application,
        notificationManager: NotificationManager,
        @Named("stopwatch")
        stopwatchNotificationBuilder: NotificationCompat.Builder,
        coroutineScope: CoroutineScope
    ): StopwatchManagerWrapper = StopwatchManagerWrapper(app, notificationManager, stopwatchNotificationBuilder, coroutineScope)

    @Provides
    @Singleton
    fun providesCountDownTimerUtil(): CountDownTimerUtil = CountDownTimerUtil()
}
