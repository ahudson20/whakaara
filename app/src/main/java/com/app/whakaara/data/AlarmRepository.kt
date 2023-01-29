package com.app.whakaara.data

import kotlinx.coroutines.flow.Flow
import java.util.*

interface AlarmRepository {

    fun allAlarms(): Flow<List<Alarm>>

    fun insert(alarm: Alarm)

    fun delete(alarm: Alarm)

    fun update(alarm: Alarm)

    fun isEnabled(id: UUID, isEnabled: Boolean)

}