package com.whakaara.data.di

import com.whakaara.data.alarm.AlarmRepository
import com.whakaara.data.alarm.AlarmRepositoryImpl
import com.whakaara.data.datastore.PreferencesDataStoreRepository
import com.whakaara.data.datastore.PreferencesDataStoreRepositoryImpl
import com.whakaara.data.preferences.PreferencesImpl
import com.whakaara.data.preferences.PreferencesRepository
import com.whakaara.data.timer.TimerRepository
import com.whakaara.data.timer.TimerRepositoryImpl
import com.whakaara.database.alarm.AlarmDao
import com.whakaara.database.datastore.PreferencesDataStore
import com.whakaara.database.preferences.PreferencesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    @Provides
    @Singleton
    fun provideAlarmRepository(alarmDao: AlarmDao): AlarmRepository =
        AlarmRepositoryImpl(
            alarmDao = alarmDao
        )

    @Provides
    @Singleton
    fun providePreferencesRepository(preferencesDao: PreferencesDao): PreferencesRepository =
        PreferencesImpl(
            preferencesDao = preferencesDao
        )

    @Provides
    @Singleton
    fun providePreferencesDataStoreRepository(preferencesDataStore: PreferencesDataStore): PreferencesDataStoreRepository =
        PreferencesDataStoreRepositoryImpl(
            preferencesDataStore = preferencesDataStore
        )

    @Provides
    @Singleton
    fun provideTimerRepository(
        preferencesDataStore: PreferencesDataStore
    ): TimerRepository = TimerRepositoryImpl(preferencesDataStore = preferencesDataStore)
}
