package com.whakaara.database.alarm

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.whakaara.database.alarm.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarmEntity: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarmEntity: AlarmEntity)

    @Query("DELETE FROM alarm_table WHERE alarmId = :id")
    suspend fun deleteAlarmById(id: UUID)

    @Update
    suspend fun updateAlarm(alarmEntity: AlarmEntity)

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarmsFlow(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): List<AlarmEntity>

    @Query("UPDATE alarm_table SET isEnabled = :isEnabled WHERE alarmId = :id")
    suspend fun isEnabled(
        id: UUID,
        isEnabled: Boolean,
    )

    @Query("SELECT * FROM alarm_table WHERE alarmId = :id")
    suspend fun getAlarmById(id: UUID): AlarmEntity
}
