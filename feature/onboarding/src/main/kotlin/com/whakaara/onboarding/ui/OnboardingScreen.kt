package com.whakaara.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.whakaara.model.onboarding.OnboardingItems
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.PreferencesState

@Composable
fun OnboardingScreen(
    navigateToHome: () -> Unit,
    pages: Array<OnboardingItems> = OnboardingItems.entries.toTypedArray(),
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingContent(
                pages = pages,
                pagerState = pagerState,
                snackbarHostState = snackbarHostState,
                navigateToHome = navigateToHome,
                preferencesState = preferencesState,
                updatePreferences = updatePreferences
            )
        }
    }
}
