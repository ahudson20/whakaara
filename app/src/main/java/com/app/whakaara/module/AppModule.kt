package com.app.whakaara.module

import android.content.Context
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

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideAlarmDatabase(
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        AlarmDatabase::class.java,
        "alarm_database"
    ).build()

    @Provides
    fun provideAlarmDao(
        alarmDatabase: AlarmDatabase
    ) = alarmDatabase.alarmDao()

    @Provides
    fun provideAlarmRepository(
        alarmDao: AlarmDao
    ): AlarmRepository = AlarmRepositoryImpl(
        alarmDao = alarmDao
    )

    @Provides
    fun providesPreferencesDataBase(
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        PreferencesDatabase::class.java,
        "preferences_database"
    ).createFromAsset("database/preferences.db").build()

    @Provides
    fun providePreferencesDao(
        preferencesDatabase: PreferencesDatabase
    ) = preferencesDatabase.preferencesDao()

    @Provides
    fun providePreferencesRepository(
        preferencesDao: PreferencesDao
    ): PreferencesRepository = PreferencesImpl(
        preferencesDao = preferencesDao
    )
}
