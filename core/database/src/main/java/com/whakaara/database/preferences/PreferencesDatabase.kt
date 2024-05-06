package com.whakaara.database.preferences

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.whakaara.database.preferences.converters.PreferencesConverter
import com.whakaara.database.preferences.entity.PreferencesEntity

@Database(entities = [PreferencesEntity::class], version = 1, exportSchema = false)
@TypeConverters(PreferencesConverter::class)
abstract class PreferencesDatabase : RoomDatabase() {
    abstract fun preferencesDao(): PreferencesDao
}
