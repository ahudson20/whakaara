package com.whakaara.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.whakaara.core.di.IoDispatcher
import com.whakaara.data.preferences.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val app: Application,
    private val preferencesRepository: PreferencesRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): AndroidViewModel(application = app) {
    fun updatePreferences() = viewModelScope.launch(ioDispatcher) {
        preferencesRepository.updateShouldShowOnboarding(
            shouldShowOnboarding = false
        )
    }
}
