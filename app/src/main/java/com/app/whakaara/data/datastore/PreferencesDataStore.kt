package com.app.whakaara.data.datastore

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
    }

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
}
