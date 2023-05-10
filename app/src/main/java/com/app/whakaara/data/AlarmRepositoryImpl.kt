package com.app.whakaara.data

import java.util.UUID

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override fun allAlarms() = alarmDao.getAllAlarms()

    override suspend fun insert(alarm: Alarm) = alarmDao.insert(alarm)

    override suspend fun delete(alarm: Alarm) = alarmDao.deleteAlarm(alarm)

    override suspend fun update(alarm: Alarm) = alarmDao.updateAlarm(alarm)

    override suspend fun isEnabled(id: UUID, isEnabled: Boolean) = alarmDao.isEnabled(id, isEnabled)

    override suspend fun getAlarmById(id: UUID): Alarm = alarmDao.getAlarmById(id)

}