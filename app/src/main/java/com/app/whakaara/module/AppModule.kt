package com.app.whakaara.module

import android.content.Context
import androidx.room.Room
import com.app.whakaara.data.AlarmDao
import com.app.whakaara.data.AlarmDatabase
import com.app.whakaara.data.AlarmRepository
import com.app.whakaara.data.AlarmRepositoryImpl
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
        context : Context
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
}