package com.whakaara.data.alarm

import com.whakaara.database.alarm.AlarmDao
import com.whakaara.database.alarm.entity.AlarmEntity
import com.whakaara.database.alarm.entity.asExternalModel
import com.whakaara.database.alarm.entity.asInternalModel
import com.whakaara.model.alarm.Alarm
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao
) : AlarmRepository {
    override fun getAllAlarmsFlow() = alarmDao.getAllAlarmsFlow().map { it.map(AlarmEntity::asExternalModel) }

    override suspend fun getAllAlarms() = alarmDao.getAllAlarms().map(AlarmEntity::asExternalModel)

    override suspend fun insert(alarm: Alarm) = alarmDao.insert(alarm.asInternalModel())

    override suspend fun delete(alarm: Alarm) = alarmDao.deleteAlarm(alarm.asInternalModel())

    override suspend fun deleteAlarmById(id: UUID) = alarmDao.deleteAlarmById(id)

    override suspend fun update(alarm: Alarm) = alarmDao.updateAlarm(alarm.asInternalModel())

    override suspend fun isEnabled(
        id: UUID,
        isEnabled: Boolean
    ) = alarmDao.isEnabled(id, isEnabled)

    override suspend fun getAlarmById(id: UUID): Alarm = alarmDao.getAlarmById(id).asExternalModel()

    private val _triggerFlow = MutableSharedFlow<Unit>(replay = 0)
    override val triggerFlow: SharedFlow<Unit> = _triggerFlow.asSharedFlow()

    override fun triggerAlarmRecreation() {
        _triggerFlow.tryEmit(Unit)
    }

    private val _deleteAlarmTriggerFlow = MutableSharedFlow<String>(replay = 0)
    override val deleteAlarmTriggerFlow: SharedFlow<String> = _deleteAlarmTriggerFlow.asSharedFlow()
    override fun triggerDeleteAlarmById(id: String) {
        _deleteAlarmTriggerFlow.tryEmit(id)
    }
}
