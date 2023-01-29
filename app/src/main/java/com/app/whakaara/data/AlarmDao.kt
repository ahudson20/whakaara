package com.app.whakaara.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface AlarmDao {

    @Insert
    fun insert(alarm: Alarm)

    @Delete
    fun deleteAlarm(alarm: Alarm)

    @Update
    fun updateAlarm(alarm: Alarm)

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Query("UPDATE alarm_table SET isEnabled = :isEnabled WHERE alarmId = :id")
    fun isEnabled(id: UUID, isEnabled: Boolean)

}