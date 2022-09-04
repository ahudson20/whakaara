package com.app.whakaara.data

import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun allAlarms(): Flow<List<Alarm>>

    fun insert(alarm: Alarm)

    fun delete(alarm: Alarm)

    fun update(alarm: Alarm)

}