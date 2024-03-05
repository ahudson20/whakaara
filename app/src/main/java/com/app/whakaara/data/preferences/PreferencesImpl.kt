package com.app.whakaara.data.preferences

import kotlinx.coroutines.flow.Flow

class PreferencesImpl(
    private val preferencesDao: PreferencesDao
) : PreferencesRepository {
    override suspend fun insert(preferences: Preferences) = preferencesDao.insert(preferences)

    override suspend fun updatePreferences(preferences: Preferences) = preferencesDao.updatePreferences(preferences)

    override fun getPreferencesFlow(): Flow<Preferences> = preferencesDao.getPreferencesFlow()

    override fun getPreferences(): Preferences = preferencesDao.getPreferences()
}
