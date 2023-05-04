package com.app.whakaara.data

import java.util.UUID

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override fun allAlarms() = alarmDao.getAllAlarms()

    override fun insert(alarm: Alarm) = alarmDao.insert(alarm)

    override fun delete(alarm: Alarm) = alarmDao.deleteAlarm(alarm)

    override fun update(alarm: Alarm) = alarmDao.updateAlarm(alarm)

    override fun isEnabled(id: UUID, isEnabled: Boolean) = alarmDao.isEnabled(id, isEnabled)

    override fun getAlarmById(id: UUID): Alarm = alarmDao.getAlarmById(id)

}