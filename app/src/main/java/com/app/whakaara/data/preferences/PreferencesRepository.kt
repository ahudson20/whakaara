package com.app.whakaara.data.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun insert(preferences: Preferences)

    suspend fun updatePreferences(preferences: Preferences)

    fun getPreferencesFlow(): Flow<Preferences>

    fun getPreferences(): Preferences
}
