package com.app.whakaara.state

import com.app.whakaara.utils.constants.DateUtilsConstants.TIMER_STARTING_FORMAT
import com.app.whakaara.utils.constants.GeneralConstants.ZERO_MILLIS

data class StopwatchState(
    val timeMillis: Long = ZERO_MILLIS,
    val lastTimeStamp: Long = ZERO_MILLIS,
    val formattedTime: String = TIMER_STARTING_FORMAT,
    val isActive: Boolean = false,
    val isStart: Boolean = true
)
