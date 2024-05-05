package com.whakaara.data.datastore

import com.whakaara.database.datastore.PreferencesDataStore
import com.whakaara.model.datastore.StopwatchDataStore
import com.whakaara.model.datastore.TimerStateDataStore
import kotlinx.coroutines.flow.Flow

class PreferencesDataStoreRepositoryImpl(
    private val preferencesDataStore: PreferencesDataStore,
) : PreferencesDataStoreRepository {
    override suspend fun readBackgroundColour(): Flow<String> {
        return preferencesDataStore.readBackgroundColour
    }

    override suspend fun readTextColour(): Flow<String> {
        return preferencesDataStore.readTextColour
    }

    override suspend fun saveColour(
        background: String,
        text: String,
    ) {
        preferencesDataStore.saveColour(background, text)
    }

    override suspend fun readTimerStatus(): Flow<TimerStateDataStore> {
        return preferencesDataStore.readTimerStatus
    }

    override suspend fun saveTimerData(state: TimerStateDataStore) {
        preferencesDataStore.saveTimerData(state)
    }

    override suspend fun readStopwatchState(): Flow<StopwatchDataStore> {
        return preferencesDataStore.readStopwatchState
    }

    override suspend fun saveStopwatchState(state: StopwatchDataStore) {
        preferencesDataStore.saveStopwatchState(state)
    }

    override suspend fun clearStopwatchState() {
        preferencesDataStore.clearStopwatchState()
    }
}
