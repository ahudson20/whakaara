package com.whakaara.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val app: Application
): AndroidViewModel(application = app) {
    // TODO: begin to migrate code over from MainViewModel
}
