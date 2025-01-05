package com.app.whakaara.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whakaara.core.di.IoDispatcher
import com.whakaara.data.preferences.PreferencesRepository
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.PreferencesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    // preferences
    private val _preferencesState = MutableStateFlow(PreferencesState())
    val preferencesUiState: StateFlow<PreferencesState> = _preferencesState.asStateFlow()

    init {
        getPreferences()
    }

    private fun getPreferences() = viewModelScope.launch {
        preferencesRepository.getPreferencesFlow().flowOn(ioDispatcher).collect { preferences ->
            _preferencesState.value = PreferencesState(preferences = preferences, isReady = true)
        }
    }

    fun updatePreferences(preferences: Preferences) = viewModelScope.launch(ioDispatcher) {
        preferencesRepository.updatePreferences(preferences = preferences)
    }
}
