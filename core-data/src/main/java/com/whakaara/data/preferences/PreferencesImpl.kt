package com.whakaara.data.preferences

import com.whakaara.database.preferences.PreferencesDao
import com.whakaara.database.preferences.entity.asExternalModel
import com.whakaara.database.preferences.entity.asInternalModel
import com.whakaara.model.preferences.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesImpl(
    private val preferencesDao: PreferencesDao
) : PreferencesRepository {
    override suspend fun insert(preferences: Preferences) = preferencesDao.insert(preferences.asInternalModel())

    override suspend fun updatePreferences(preferences: Preferences) = preferencesDao.updatePreferences(preferences.asInternalModel())

    override fun getPreferencesFlow(): Flow<Preferences> = preferencesDao.getPreferencesFlow().map { it.asExternalModel() }

    override fun getPreferences(): Preferences = preferencesDao.getPreferences().asExternalModel()
}
