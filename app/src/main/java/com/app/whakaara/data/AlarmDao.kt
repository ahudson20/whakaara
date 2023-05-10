package com.app.whakaara.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Query("UPDATE alarm_table SET isEnabled = :isEnabled WHERE alarmId = :id")
    suspend fun isEnabled(id: UUID, isEnabled: Boolean)

    @Query("SELECT * FROM alarm_table WHERE alarmId = :id")
    suspend fun getAlarmById(id: UUID): Alarm
}