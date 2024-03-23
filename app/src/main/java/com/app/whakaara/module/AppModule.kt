package com.app.whakaara.module

import android.content.Context
import android.os.PowerManager
import androidx.room.Room
import com.app.whakaara.data.alarm.AlarmDao
import com.app.whakaara.data.alarm.AlarmDatabase
import com.app.whakaara.data.alarm.AlarmRepository
import com.app.whakaara.data.alarm.AlarmRepositoryImpl
import com.app.whakaara.data.preferences.PreferencesDao
import com.app.whakaara.data.preferences.PreferencesDatabase
import com.app.whakaara.data.preferences.PreferencesImpl
import com.app.whakaara.data.preferences.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideAlarmDatabase(
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        AlarmDatabase::class.java,
        "alarm_database"
    ).build()

    @Provides
    @Singleton
    fun provideAlarmDao(
        alarmDatabase: AlarmDatabase
    ) = alarmDatabase.alarmDao()

    @Provides
    @Singleton
    fun provideAlarmRepository(
        alarmDao: AlarmDao
    ): AlarmRepository = AlarmRepositoryImpl(
        alarmDao = alarmDao
    )

    @Provides
    @Singleton
    fun providesPreferencesDataBase(
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        PreferencesDatabase::class.java,
        "preferences_database"
    ).createFromAsset("database/preferences.db").build()

    @Provides
    @Singleton
    fun providePreferencesDao(
        preferencesDatabase: PreferencesDatabase
    ) = preferencesDatabase.preferencesDao()

    @Provides
    @Singleton
    fun providePreferencesRepository(
        preferencesDao: PreferencesDao
    ): PreferencesRepository = PreferencesImpl(
        preferencesDao = preferencesDao
    )

    @Provides
    fun providesPowerManager(
        @ApplicationContext
        context: Context
    ): PowerManager = (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
}
