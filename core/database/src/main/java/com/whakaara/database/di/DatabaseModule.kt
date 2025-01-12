package com.whakaara.database.di

import android.content.Context
import androidx.room.Room
import com.whakaara.database.alarm.AlarmDatabase
import com.whakaara.database.preferences.PreferencesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
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
    fun provideAlarmDao(alarmDatabase: AlarmDatabase) = alarmDatabase.alarmDao()

    @Provides
    @Singleton
    fun providesPreferencesDataBase(
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        PreferencesDatabase::class.java,
        "preferences_database"
    ).createFromAsset("database/preferences.db")
        .build()

    @Provides
    @Singleton
    fun providePreferencesDao(preferencesDatabase: PreferencesDatabase) = preferencesDatabase.preferencesDao()
}
