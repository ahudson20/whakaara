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
import com.app.whakaara.state.TimerStateDataStore
import com.app.whakaara.ui.theme.darkGreen
import com.app.whakaara.utils.GeneralUtils
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
            remainingTimeInMillis = preferences[PreferencesKeys.TIMER_FINISH_KEY] ?: 0L,
            isActive = preferences[PreferencesKeys.TIMER_ACTIVE_KEY] ?: false,
            isPaused = preferences[PreferencesKeys.TIMER_PAUSED_KEY] ?: false,
            timeStamp = preferences[PreferencesKeys.TIMER_TIME_STAMP] ?: 0L
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
}
