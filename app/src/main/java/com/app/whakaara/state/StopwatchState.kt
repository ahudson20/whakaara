package com.app.whakaara.state

import com.app.whakaara.utils.constants.DateUtilsConstants
import com.app.whakaara.utils.constants.GeneralConstants

data class StopwatchState(
    val timeMillis: Long = GeneralConstants.ZERO_MILLIS,
    val lastTimeStamp: Long = GeneralConstants.ZERO_MILLIS,
    val formattedTime: String = DateUtilsConstants.TIMER_STARTING_FORMAT,
    val isActive: Boolean = false,
    val isStart: Boolean = true
)
