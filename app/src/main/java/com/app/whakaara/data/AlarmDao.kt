package com.app.whakaara.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: Alarm)

    @Delete
    fun deleteAlarm(alarm: Alarm)

    @Update
    fun updateAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Query("UPDATE alarm_table SET isEnabled = :isEnabled WHERE alarmId = :id")
    fun isEnabled(id: UUID, isEnabled: Boolean)

    @Query("SELECT * FROM alarm_table WHERE alarmId = :id")
    fun getAlarmById(id: UUID): Alarm
}