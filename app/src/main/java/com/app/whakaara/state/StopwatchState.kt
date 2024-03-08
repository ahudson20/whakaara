package com.app.whakaara.state

import com.app.whakaara.utils.constants.DateUtilsConstants.STOPWATCH_STARTING_TIME
import com.app.whakaara.utils.constants.GeneralConstants.ZERO_MILLIS

data class StopwatchState(
    val timeMillis: Long = ZERO_MILLIS,
    val lastTimeStamp: Long = ZERO_MILLIS,
    val formattedTime: String = STOPWATCH_STARTING_TIME,
    val isActive: Boolean = false,
    val isStart: Boolean = true
)
