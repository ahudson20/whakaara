package com.app.whakaara.data

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface AlarmRepository {

    fun allAlarms(): Flow<List<Alarm>>

    suspend fun insert(alarm: Alarm)

    suspend fun delete(alarm: Alarm)

    suspend fun update(alarm: Alarm)

    suspend fun isEnabled(id: UUID, isEnabled: Boolean)

    suspend fun getAlarmById(id: UUID): Alarm

}