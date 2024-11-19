package com.whakaara.model.timer

import com.whakaara.core.constants.DateUtilsConstants
import com.whakaara.core.constants.GeneralConstants

data class TimerState(
    val currentTime: Long = GeneralConstants.ZERO_MILLIS,
    val inputHours: String = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
    val inputMinutes: String = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
    val inputSeconds: String = DateUtilsConstants.TIMER_INPUT_INITIAL_VALUE,
    val isTimerPaused: Boolean = false,
    val isStart: Boolean = true,
    val isTimerActive: Boolean = false,
    val progress: Float = GeneralConstants.STARTING_CIRCULAR_PROGRESS,
    val time: String = DateUtilsConstants.TIMER_STARTING_FORMAT,
    val millisecondsFromTimerInput: Long = GeneralConstants.ZERO_MILLIS
)
