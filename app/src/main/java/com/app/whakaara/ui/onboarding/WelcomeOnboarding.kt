package com.app.whakaara.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.whakaara.R
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings.space20
import com.app.whakaara.ui.theme.Spacings.space200
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import com.app.whakaara.ui.theme.lightBlueAnimation

@Composable
fun WelcomeOnboarding() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = spaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(space200))
        Box(
            Modifier
                .size(space200)
                .clip(RoundedCornerShape(25.dp))
                .background(color = lightBlueAnimation),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(id = R.drawable.translucent),
                contentDescription = stringResource(id = R.string.delete_icon_content_description)
            )
        }
        Spacer(modifier = Modifier.height(space20))
        Text(
            modifier = Modifier.width(300.dp),
            text = stringResource(id = R.string.onboarding_welcome_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            modifier = Modifier.width(300.dp),
            text = stringResource(id = R.string.onboarding_welcome_sub_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun WelcomeOnboardingPreview() {
    WhakaaraTheme {
        WelcomeOnboarding()
    }
}
