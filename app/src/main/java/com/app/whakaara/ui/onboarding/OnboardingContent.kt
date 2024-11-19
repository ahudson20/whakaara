package com.app.whakaara.ui.onboarding

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.app.whakaara.R
import com.whakaara.core.designsystem.theme.FontScalePreviews
import com.whakaara.core.designsystem.theme.Spacings.space80
import com.whakaara.core.designsystem.theme.Spacings.spaceMedium
import com.whakaara.core.designsystem.theme.Spacings.spaceSmall
import com.whakaara.core.designsystem.theme.Spacings.spaceXLarge
import com.whakaara.core.designsystem.theme.Spacings.spaceXxSmall
import com.whakaara.core.designsystem.theme.ThemePreviews
import com.whakaara.core.designsystem.theme.WhakaaraTheme
import com.whakaara.model.onboarding.OnboardingItems
import com.whakaara.model.preferences.Preferences
import com.whakaara.model.preferences.PreferencesState
import kotlinx.coroutines.launch

@Composable
fun OnboardingContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    pages: Array<OnboardingItems>,
    snackbarHostState: SnackbarHostState,
    navigateToHome: () -> Unit,
    preferencesState: PreferencesState,
    updatePreferences: (preferences: Preferences) -> Unit
) {
    val scope = rememberCoroutineScope()
    Column(modifier = modifier.fillMaxSize()) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { index ->
            when (pages[index]) {
                OnboardingItems.WELCOME -> WelcomeOnboarding()
                OnboardingItems.NOTIFICATIONS -> NotificationsOnboarding(snackbarHostState = snackbarHostState)
                OnboardingItems.BATTERY_OPTIMIZATION -> DisableBatteryOptimizationOnboarding()
                OnboardingItems.WIDGET -> WidgetOnboarding()
            }
        }

        BottomSection(
            pagesSize = pages.size,
            pagerState = pagerState
        ) {
            if (pagerState.currentPage == pages.size - 1) {
                updatePreferences(
                    preferencesState.preferences.copy(
                        shouldShowOnboarding = false
                    )
                )
                navigateToHome()
            } else {
                scope.launch {
                    pagerState.animateScrollToPage(
                        page = pagerState.currentPage + 1,
                        animationSpec = tween()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomSection(
    pagesSize: Int,
    pagerState: PagerState,
    onButtonClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(space80)
            .padding(
                vertical = spaceMedium,
                horizontal = spaceXLarge
            )
    ) {
        PageIndicators(
            pagesSize = pagesSize,
            pagerState = pagerState
        )

        Button(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = onButtonClick
        ) {
            Text(
                text = if (pagerState.currentPage == pagesSize - 1) {
                    stringResource(id = R.string.onboarding_button_complete)
                } else {
                    stringResource(id = R.string.onboarding_button_next)
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxScope.PageIndicators(
    pagesSize: Int,
    pagerState: PagerState
) {
    Row(
        modifier = Modifier.align(Alignment.CenterStart),
        horizontalArrangement = Arrangement.spacedBy(spaceSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pagesSize) { iteration ->
            Indicator(isSelected = pagerState.currentPage == iteration)
        }
    }
}

@Composable
private fun Indicator(isSelected: Boolean) {
    val size =
        animateDpAsState(
            targetValue = if (isSelected) spaceMedium else spaceSmall,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "indicator size"
        )
    val color = if (isSelected) com.whakaara.core.designsystem.theme.darkGreen else com.whakaara.core.designsystem.theme.darkGreen.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .padding(spaceXxSmall)
            .clip(CircleShape)
            .background(color)
            .size(size.value)
    )
}

@Composable
@ThemePreviews
@FontScalePreviews
fun OnboardingContentPreview() {
    val pages = OnboardingItems.entries.toTypedArray()
   WhakaaraTheme {
        OnboardingContent(
            pages = pages,
            pagerState = rememberPagerState(pageCount = { pages.size }),
            snackbarHostState = remember { SnackbarHostState() },
            navigateToHome = {},
            preferencesState = PreferencesState(),
            updatePreferences = {}
        )
    }
}
