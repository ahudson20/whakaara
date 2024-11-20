package com.whakaara.data.preferences

import com.whakaara.model.preferences.Preferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun insert(preferences: Preferences)

    suspend fun updatePreferences(preferences: Preferences)

    fun getPreferencesFlow(): Flow<Preferences>

    fun getPreferences(): Preferences

    suspend fun updateShouldShowOnboarding(shouldShowOnboarding: Boolean)
}
