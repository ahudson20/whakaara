package com.whakaara.core.widget

import android.content.Context
import android.content.Intent
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.ContentScale
import androidx.glance.layout.size
import com.whakaara.core.constants.GeneralConstants.MAIN_ACTIVITY
import com.whakaara.core.designsystem.theme.Spacings.spaceXxLarge
import com.whakaara.core.designsystem.theme.WidgetTheme

class AppShortcutWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val deepLinkIntent = Intent(Intent.ACTION_VIEW).apply {
            setClassName(
                context.packageName,
                MAIN_ACTIVITY
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        provideContent {
            GlanceTheme(colors = WidgetTheme.colors) {
                Image(
                    modifier = GlanceModifier.size(spaceXxLarge)
                        .clickable(actionStartActivity(deepLinkIntent)),
                    contentDescription = LocalContext.current.getString(R.string.widget_next_alarm_icon_description),
                    provider = ImageProvider(R.drawable.outline_alarm_24),
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(WidgetTheme.colors.tertiary)
                )
            }
        }
    }
}
