package com.app.whakaara.data.preferences

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preferences: Preferences)

    @Update
    suspend fun updatePreferences(preferences: Preferences)

    @Query("SELECT * FROM preferences_table")
    fun getPreferencesFlow(): Flow<Preferences>

    @Query("SELECT * FROM preferences_table")
    fun getPreferences(): Preferences
}
