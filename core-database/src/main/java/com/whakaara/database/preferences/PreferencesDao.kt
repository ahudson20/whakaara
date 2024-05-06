package com.whakaara.database.preferences

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.whakaara.database.preferences.entity.PreferencesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preferencesEntity: PreferencesEntity)

    @Update
    suspend fun updatePreferences(preferencesEntity: PreferencesEntity)

    @Query("SELECT * FROM preferences_table")
    fun getPreferencesFlow(): Flow<PreferencesEntity>

    @Query("SELECT * FROM preferences_table")
    fun getPreferences(): PreferencesEntity
}
