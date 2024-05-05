package com.app.whakaara.state

import com.whakaara.core.constants.DateUtilsConstants.STOPWATCH_STARTING_TIME
import com.whakaara.core.constants.GeneralConstants.ZERO_MILLIS
import com.whakaara.model.datastore.StopwatchDataStore
import com.whakaara.model.stopwatch.Lap

data class StopwatchState(
    val timeMillis: Long = ZERO_MILLIS,
    val lastTimeStamp: Long = ZERO_MILLIS,
    val formattedTime: String = STOPWATCH_STARTING_TIME,
    val isActive: Boolean = false,
    val isStart: Boolean = true,
    val isPaused: Boolean = false,
    val lapList: MutableList<Lap> = mutableListOf(),
)

fun StopwatchDataStore.asExternalModel() =
    StopwatchState(
        timeMillis = timeMillis,
        lastTimeStamp = lastTimeStamp,
        formattedTime = formattedTime,
        isActive = isActive,
        isStart = isStart,
        isPaused = isPaused,
        lapList = lapList,
    )

fun StopwatchState.asInternalModel() =
    StopwatchDataStore(
        timeMillis = timeMillis,
        lastTimeStamp = lastTimeStamp,
        formattedTime = formattedTime,
        isActive = isActive,
        isStart = isStart,
        isPaused = isPaused,
        lapList = lapList,
    )
