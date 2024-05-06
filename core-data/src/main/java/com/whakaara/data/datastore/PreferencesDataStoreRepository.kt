package com.whakaara.data.datastore

import com.whakaara.model.datastore.StopwatchDataStore
import com.whakaara.model.datastore.TimerStateDataStore
import kotlinx.coroutines.flow.Flow

interface PreferencesDataStoreRepository {
    suspend fun readBackgroundColour(): Flow<String>

    suspend fun readTextColour(): Flow<String>

    suspend fun saveColour(
        background: String,
        text: String
    )

    suspend fun readTimerStatus(): Flow<TimerStateDataStore>

    suspend fun saveTimerData(state: TimerStateDataStore)

    suspend fun readStopwatchState(): Flow<StopwatchDataStore>

    suspend fun saveStopwatchState(state: StopwatchDataStore)

    suspend fun clearStopwatchState()
}
