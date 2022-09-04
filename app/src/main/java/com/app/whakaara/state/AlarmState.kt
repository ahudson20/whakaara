package com.app.whakaara.state

import com.app.whakaara.data.Alarm

/**
 * ?unsure how im going to setup the state yet..
 * **/
data class AlarmState(
    val alarms: List<Alarm> = emptyList(),
    val id: Int = 0,
    val hour: Int = 0,
    val minute: Int = 0,
    val title: String? = null,
    val isVibrate: Boolean = false,
) {
//    fun toUiState(): AlarmUiState {
//        return when
//    }
}

sealed interface AlarmUiState {

}