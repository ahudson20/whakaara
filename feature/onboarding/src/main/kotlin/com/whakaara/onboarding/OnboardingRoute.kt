package com.whakaara.onboarding

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.whakaara.onboarding.ui.OnboardingScreen

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    OnboardingScreen()
}
