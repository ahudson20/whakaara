package com.whakaara.model.datastore

data class TimerStateDataStore(
    val remainingTimeInMillis: Long = 0L,
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val timeStamp: Long = 0L,
)
