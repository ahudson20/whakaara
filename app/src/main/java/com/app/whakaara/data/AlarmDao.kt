package com.app.whakaara.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

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

}