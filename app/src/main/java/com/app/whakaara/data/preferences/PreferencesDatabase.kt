package com.app.whakaara.data.preferences

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Preferences::class], version = 1, exportSchema = false)
abstract class PreferencesDatabase : RoomDatabase() {
    abstract fun preferencesDao(): PreferencesDao
}
