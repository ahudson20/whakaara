package com.app.whakaara.state

import com.whakaara.model.alarm.Alarm

sealed class AlarmState {
    data object Loading : AlarmState()

    data class Success(val alarms: List<Alarm> = emptyList()) : AlarmState()
}
