package com.whakaara.model.datastore

import com.whakaara.core.constants.DateUtilsConstants

data class TimerStateDataStore(
    val remainingTimeInMillis: Long = 0L,
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val timeStamp: Long = 0L,
    val inputHours: String = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
    val inputMinutes: String = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
    val inputSeconds: String = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE
)
