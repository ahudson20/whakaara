package com.app.whakaara.data.datastore

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.whakaara.state.Lap
import com.app.whakaara.state.StopwatchState
import com.app.whakaara.state.TimerStateDataStore
import com.app.whakaara.ui.theme.darkGreen
import com.app.whakaara.utils.GeneralUtils
import com.app.whakaara.utils.constants.DateUtilsConstants.STOPWATCH_STARTING_TIME
import com.app.whakaara.utils.constants.GeneralConstants.ZERO_MILLIS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore("settings")

@Singleton
class PreferencesDataStore @Inject constructor(
    @ApplicationContext
    context: Context
) {
    private val preferencesDataStore = context.preferencesDataStore

    private object PreferencesKeys {
        val COLOUR_BACKGROUND_KEY = stringPreferencesKey("colour_background")
        val COLOUR_TEXT_KEY = stringPreferencesKey("colour_text")

        val TIMER_FINISH_KEY = longPreferencesKey("timer_finish")
        val TIMER_ACTIVE_KEY = booleanPreferencesKey("timer_active")
        val TIMER_PAUSED_KEY = booleanPreferencesKey("timer_paused")
        val TIMER_TIME_STAMP = longPreferencesKey("timer_time_stamp")

        // TODO: Shift to Room
        val STOPWATCH_TIME_MILLIS = longPreferencesKey("stopwatch_time")
        val STOPWATCH_LAST_TIMESTAMP = longPreferencesKey("stopwatch_timestamp")
        val STOPWATCH_FORMATTED_TIME = stringPreferencesKey("stopwatch_formattedTime")
        val STOPWATCH_IS_ACTIVE = booleanPreferencesKey("stopwatch_is_active")
        val STOPWATCH_IS_START = booleanPreferencesKey("stopwatch_is_start")
        val STOPWATCH_IS_PAUSED = booleanPreferencesKey("stopwatch_is_paused")
        val STOPWATCH_LAP_LIST = stringPreferencesKey("stopwatch_lap_list")
    }

    //region colour
    val readBackgroundColour = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.COLOUR_BACKGROUND_KEY] ?: GeneralUtils.convertColourToString(colour = darkGreen)
    }

    val readTextColour = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.COLOUR_TEXT_KEY] ?: GeneralUtils.convertColourToString(colour = Color.White)
    }

    suspend fun saveColour(background: String, text: String) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.COLOUR_BACKGROUND_KEY] = background
            preferences[PreferencesKeys.COLOUR_TEXT_KEY] = text
        }
    }
    //endregion

    //region timer
    val readTimerStatus = preferencesDataStore.data.map { preferences ->
        TimerStateDataStore(
            remainingTimeInMillis = preferences[PreferencesKeys.TIMER_FINISH_KEY] ?: ZERO_MILLIS,
            isActive = preferences[PreferencesKeys.TIMER_ACTIVE_KEY] ?: false,
            isPaused = preferences[PreferencesKeys.TIMER_PAUSED_KEY] ?: false,
            timeStamp = preferences[PreferencesKeys.TIMER_TIME_STAMP] ?: ZERO_MILLIS
        )
    }

    suspend fun saveTimerData(state: TimerStateDataStore) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.TIMER_FINISH_KEY] = state.remainingTimeInMillis
            preferences[PreferencesKeys.TIMER_ACTIVE_KEY] = state.isActive
            preferences[PreferencesKeys.TIMER_PAUSED_KEY] = state.isPaused
            preferences[PreferencesKeys.TIMER_TIME_STAMP] = state.timeStamp
        }
    }
    //endregion

    //region stopwatch
    val readStopwatchState = preferencesDataStore.data.map { preferences ->
        val lapString = preferences[PreferencesKeys.STOPWATCH_LAP_LIST] ?: "[]"
        val list: ArrayList<Lap> = Gson().fromJson(lapString, object : TypeToken<ArrayList<Lap>>() {}.type)
        StopwatchState(
            timeMillis = preferences[PreferencesKeys.STOPWATCH_TIME_MILLIS] ?: ZERO_MILLIS,
            lastTimeStamp = preferences[PreferencesKeys.STOPWATCH_LAST_TIMESTAMP] ?: ZERO_MILLIS,
            formattedTime = preferences[PreferencesKeys.STOPWATCH_FORMATTED_TIME] ?: STOPWATCH_STARTING_TIME,
            isActive = preferences[PreferencesKeys.STOPWATCH_IS_ACTIVE] ?: false,
            isStart = preferences[PreferencesKeys.STOPWATCH_IS_START] ?: true,
            isPaused = preferences[PreferencesKeys.STOPWATCH_IS_PAUSED] ?: false,
            lapList = list
        )
    }

    suspend fun saveStopwatchState(state: StopwatchState) {
        val list = Gson().toJson(state.lapList)
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.STOPWATCH_TIME_MILLIS] = state.timeMillis
            preferences[PreferencesKeys.STOPWATCH_LAST_TIMESTAMP] = state.lastTimeStamp
            preferences[PreferencesKeys.STOPWATCH_FORMATTED_TIME] = state.formattedTime
            preferences[PreferencesKeys.STOPWATCH_IS_ACTIVE] = state.isActive
            preferences[PreferencesKeys.STOPWATCH_IS_START] = state.isStart
            preferences[PreferencesKeys.STOPWATCH_IS_PAUSED] = state.isPaused
            preferences[PreferencesKeys.STOPWATCH_LAP_LIST] = list
        }
    }

    suspend fun clearStopwatchState() {
        preferencesDataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.STOPWATCH_TIME_MILLIS)
            preferences.remove(PreferencesKeys.STOPWATCH_LAST_TIMESTAMP)
            preferences.remove(PreferencesKeys.STOPWATCH_FORMATTED_TIME)
            preferences.remove(PreferencesKeys.STOPWATCH_IS_ACTIVE)
            preferences.remove(PreferencesKeys.STOPWATCH_IS_START)
            preferences.remove(PreferencesKeys.STOPWATCH_IS_PAUSED)
            preferences.remove(PreferencesKeys.STOPWATCH_LAP_LIST)
        }
    }
    //endregion
}
