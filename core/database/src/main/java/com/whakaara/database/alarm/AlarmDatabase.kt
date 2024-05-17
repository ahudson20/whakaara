package com.whakaara.database.alarm

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.whakaara.database.alarm.converters.AlarmConverter
import com.whakaara.database.alarm.entity.AlarmEntity

@Database(entities = [AlarmEntity::class], version = 1, exportSchema = false)
@TypeConverters(AlarmConverter::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}
