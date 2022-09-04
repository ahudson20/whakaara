package com.app.whakaara.data

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override fun allAlarms() = alarmDao.getAllAlarms()

    override fun insert(alarm: Alarm) = alarmDao.insert(alarm)

    override fun delete(alarm: Alarm) = alarmDao.deleteAlarm(alarm)

    override fun update(alarm: Alarm) = alarmDao.updateAlarm(alarm)
}