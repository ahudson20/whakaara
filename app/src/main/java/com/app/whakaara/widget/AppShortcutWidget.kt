package com.app.whakaara.widget

import android.content.Context
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.ContentScale
import androidx.glance.layout.size
import com.app.whakaara.R
import com.app.whakaara.activities.MainActivity
import com.app.whakaara.ui.theme.Spacings.spaceXxLarge
import com.app.whakaara.ui.theme.WidgetTheme

class AppShortcutWidget : GlanceAppWidget() {
    @OptIn(ExperimentalLayoutApi::class)
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        provideContent {
            GlanceTheme(colors = WidgetTheme.colors) {
                Image(
                    modifier = GlanceModifier.size(spaceXxLarge).clickable(actionStartActivity<MainActivity>()),
                    contentDescription = LocalContext.current.getString(R.string.widget_next_alarm_icon_description),
                    provider = ImageProvider(R.drawable.outline_alarm_24),
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(WidgetTheme.colors.tertiary),
                )
            }
        }
    }
}

class AppShortcutWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AppShortcutWidget()
}
