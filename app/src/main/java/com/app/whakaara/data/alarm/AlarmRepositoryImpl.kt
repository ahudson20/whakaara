package com.app.whakaara.data.alarm

import java.util.UUID

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
) : AlarmRepository {
    override fun getAllAlarmsFlow() = alarmDao.getAllAlarmsFlow()

    override suspend fun getAllAlarms() = alarmDao.getAllAlarms()

    override suspend fun insert(alarm: Alarm) = alarmDao.insert(alarm)

    override suspend fun delete(alarm: Alarm) = alarmDao.deleteAlarm(alarm)

    override suspend fun deleteAlarmById(id: UUID) = alarmDao.deleteAlarmById(id)

    override suspend fun update(alarm: Alarm) = alarmDao.updateAlarm(alarm)

    override suspend fun isEnabled(id: UUID, isEnabled: Boolean) = alarmDao.isEnabled(id, isEnabled)

    override suspend fun getAlarmById(id: UUID): Alarm = alarmDao.getAlarmById(id)
}
