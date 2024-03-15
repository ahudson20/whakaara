package com.app.whakaara.state

import com.app.whakaara.data.alarm.Alarm
sealed class AlarmState {
    object Loading : AlarmState()
    data class Success(val alarms: List<Alarm> = emptyList()) : AlarmState()
}
