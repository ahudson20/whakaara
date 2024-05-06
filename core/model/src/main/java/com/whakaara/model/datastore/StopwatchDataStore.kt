package com.whakaara.model.datastore

import com.whakaara.core.constants.DateUtilsConstants
import com.whakaara.core.constants.GeneralConstants
import com.whakaara.model.stopwatch.Lap

data class StopwatchDataStore(
    val timeMillis: Long = GeneralConstants.ZERO_MILLIS,
    val lastTimeStamp: Long = GeneralConstants.ZERO_MILLIS,
    val formattedTime: String = DateUtilsConstants.STOPWATCH_STARTING_TIME,
    val isActive: Boolean = false,
    val isStart: Boolean = true,
    val isPaused: Boolean = false,
    val lapList: MutableList<Lap> = mutableListOf()
)
