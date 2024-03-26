package com.app.whakaara.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.app.whakaara.R
import com.app.whakaara.receiver.AppWidgetReceiver
import com.app.whakaara.ui.theme.FontScalePreviews
import com.app.whakaara.ui.theme.Spacings
import com.app.whakaara.ui.theme.Spacings.space200
import com.app.whakaara.ui.theme.Spacings.spaceMedium
import com.app.whakaara.ui.theme.ThemePreviews
import com.app.whakaara.ui.theme.WhakaaraTheme
import kotlinx.coroutines.launch

@Composable
fun WidgetOnboarding() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spaceMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(space200))
        Text(
            modifier = Modifier.width(300.dp),
            text = stringResource(id = R.string.onboarding_widget_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            modifier = Modifier.width(300.dp),
            text = stringResource(id = R.string.onboarding_widget_sub_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            modifier = Modifier.width(300.dp),
            text = stringResource(id = R.string.onboarding_widget_sub_text_second),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(Spacings.space20))
        Button(
            onClick = {
                scope.launch {
                    GlanceAppWidgetManager(context).requestPinGlanceAppWidget(
                        receiver = AppWidgetReceiver::class.java
                    )
                }
            }
        ) {
            Text(text = stringResource(id = R.string.onboarding_widget_button))
        }
    }
}

@Composable
@ThemePreviews
@FontScalePreviews
fun WidgetOnboardingPreview() {
    WhakaaraTheme {
        WidgetOnboarding()
    }
}
