package com.app.whakaara.data.preferences

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.whakaara.data.preferences.converters.PreferencesConverter

@Database(entities = [Preferences::class], version = 1, exportSchema = false)
@TypeConverters(PreferencesConverter::class)
abstract class PreferencesDatabase : RoomDatabase() {
    abstract fun preferencesDao(): PreferencesDao
}
