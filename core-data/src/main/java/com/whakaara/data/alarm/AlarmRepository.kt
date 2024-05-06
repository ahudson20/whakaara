package com.whakaara.data.alarm

import com.whakaara.model.alarm.Alarm
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface AlarmRepository {
    fun getAllAlarmsFlow(): Flow<List<Alarm>>

    suspend fun getAllAlarms(): List<Alarm>

    suspend fun insert(alarm: Alarm)

    suspend fun delete(alarm: Alarm)

    suspend fun deleteAlarmById(id: UUID)

    suspend fun update(alarm: Alarm)

    suspend fun isEnabled(
        id: UUID,
        isEnabled: Boolean
    )

    suspend fun getAlarmById(id: UUID): Alarm
}
