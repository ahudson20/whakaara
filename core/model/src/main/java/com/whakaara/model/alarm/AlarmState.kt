package com.whakaara.model.alarm

sealed class AlarmState {
    data object Loading : AlarmState()

    data class Success(val alarms: List<Alarm> = emptyList()) : AlarmState()
}
